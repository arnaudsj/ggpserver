/*
    Copyright (C) 2008,2009 Martin Günther <mintar@gmx.de>

    This file is part of GgpRatingSystem.

    GgpRatingSystem is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GgpRatingSystem is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GgpRatingSystem.  If not, see <http://www.gnu.org/licenses/>.
*/

package ggpratingsystem;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import au.com.bytecode.opencsv.CSVReader;

public class FileMatchSetReader implements MatchSetReader {
	private static final Logger log = Logger.getLogger(FileMatchSetReader.class.getName());
	private Map<MatchSet, List<File>> unreadMatches = new HashMap<MatchSet, List<File>>();
	private List<MatchSet> unreadMatchSets = new LinkedList<MatchSet>();
	private FileMatchReader fileMatchReader;
	private Configuration configuration;
	
	static {
		// inherit default level for package ggpratingsystem
		log.setLevel(null);
	}
		

	public FileMatchSetReader(File directory, Configuration configuration) throws IOException {
		this.configuration = configuration;
		this.fileMatchReader = new FileMatchReader(configuration.getPlayerSet());
		initialize(directory);
	}
	
//	public static List<MatchSet> readDataDir(String subdirectory) throws IOException {
//		return readMatches(new File(Util.getDataDir(), subdirectory));
//		
//	}
	
	/* (non-Javadoc)
	 * @see ggpratingsystem.MatchReader#readMatchSet()
	 */
//	@Override
	public MatchSet readMatchSet() {
		MatchSet matchSet = unreadMatchSets.get(0);
		List<File> fileList = unreadMatches.get(matchSet);
		
		log.info("Reading match set (" + fileList.size() + " XML files).");
		
    	/* create the Matches and add them to the MatchSet */
		for (File xmlFile : fileList) {
			try {
				Match match = fileMatchReader.readMatch(matchSet, xmlFile);
		    	matchSet.addMatch(match);
			} catch (MatchParsingException e) {
				log.warning("Error while reading XML file: " + xmlFile + "\nIgnoring this file.");
				log.throwing(Match.class.getName(), "<init>", e);
			}
		}
		
		unreadMatchSets.remove(0);
		unreadMatches.remove(matchSet);
		
		return matchSet;
	}
	
	/* (non-Javadoc)
	 * @see ggpratingsystem.MatchReader#hasNext()
	 */
//	@Override
	public boolean hasNext() {
		return ! unreadMatchSets.isEmpty();
	}
	
	/**
	 * Opens the match_index.csv from the given directory, reads in all lines, and
	 * prepares all MatchSets. Does not read the XML files here (time saving). This
	 * is done in readMatchSet().
	 */
	private void initialize(File directory) throws IOException {
		Map<String, MatchSet> idsToMatchSets = new HashMap<String, MatchSet>();
		int matchSetNumber = 0;	
		
	    /* look for a file called match_index.csv and try to read the match set data from it */
		log.info("Reading match_index.csv.");
		
		CSVReader reader;
		File matchIndexFile = new File(directory, "match_index.csv");
		reader = new CSVReader(new FileReader(matchIndexFile));

		List<String[]> lines = new LinkedList<String[]>();
		for (Iterator iter = reader.readAll().iterator(); iter.hasNext();) {
			String[] line = (String[]) iter.next();
			lines.add(line);
		}		
		
		/* prepare unreadMatchSets */
	    for (String[] line : lines) {	// Match ID; Game; Year; Round; Day
	    	if (line.length != 5) {
	    		throw new  IllegalArgumentException("Incorrect number of arguments in CSV file " + matchIndexFile);
	    	}
	    	
	    	String matchID = line[0];	    	
	    	String gameName = line[1];
	    	int year = Integer.parseInt(line[2]);
	    	int round = Integer.parseInt(line[3]);
	    	int day =  Integer.parseInt(line[4]);
	    	
    		String matchSetID = gameName + "_" + year + "_R" + round + "_D" + day;
	    	/* 
	    	 * This assumes, of course, that no two different MatchSets are played 
	    	 * with the same game on the same day. But should be ok for now. 
	    	 */
	    	
	    	/* retrieve or create the corresponding MatchSet */
	    	MatchSet matchSet = idsToMatchSets.get(matchSetID);
	    	if (matchSet == null) {
	    		matchSetNumber++;
	    		Game game = configuration.getGameSet().getGame(gameName);
	    		matchSet = new MatchSet(matchSetID, year, round, day, matchSetNumber, game);
	    		
	    		idsToMatchSets.put(matchSetID, matchSet);
	    		unreadMatchSets.add(matchSet);
	    		unreadMatches.put(matchSet, new LinkedList<File>());
	    	}
	    	
	    	/* add the unprocessed xml file name to the list of unread matches for this MatchSet*/
	    	File xmlFile = new File(directory, matchID + ".xml");	    	
	    	unreadMatches.get(matchSet).add(xmlFile);
		}
	}
}
