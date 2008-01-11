package ggpratingsystem;

import ggpratingsystem.util.Util;

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

public class MatchReader {
	private static final Logger log = Logger.getLogger(MatchReader.class.getName());
	
	static {
		// inherit default level for package ggpratingsystem
		log.setLevel(null);	// TODO: do this everywhere where a logger is needed
	}

	public static List<MatchSet> readMatches(File directory) throws IOException {
		// this ordered List will be returned
		List<MatchSet> matchSets = new LinkedList<MatchSet>();

		// this map is only temporary and stores the same MatchSets as the list above,
		// but it's more efficient to retrieve stuff from a map
		Map<String, MatchSet> idsToMatchSets = new HashMap<String, MatchSet>();
		
		int matchSetNumber = 0;	
		// this should be reset at the "beginning" of a new day,
		// but this stuff is temporary anyway
		
	    /* look for a file called match_index.csv and try to read the match set data from it */
		CSVReader reader;
		File matchIndexFile = new File(directory, "match_index.csv");
		reader = new CSVReader(new FileReader(matchIndexFile));

		List<String[]> lines = new LinkedList<String[]>();
		for (Iterator iter = reader.readAll().iterator(); iter.hasNext();) {
			String[] line = (String[]) iter.next();
			lines.add(line);
		}
		
	    if (lines.size() > 10) {
			log.info("Reading " + lines.size() + " XML files, this may take a while...");
	    }
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
	    		Game game = Game.getInstance(gameName);
	    		matchSet = new MatchSet(matchSetID, year, round, day, matchSetNumber, game);
	    		
	    		matchSets.add(matchSet);
	    		idsToMatchSets.put(matchSetID, matchSet);
	    	}
	    	
	    	/* create the Match and add it to the MatchSet */
	    	File xmlFile = new File(directory, matchID + ".xml");
			try {
				Match match = new Match(matchSet, xmlFile);
		    	matchSet.addMatch(match);
			} catch (MatchParsingException e) {
				log.warning("Error while reading XML file: " + xmlFile + "\nIgnoring this file.");
				log.throwing(Match.class.getName(), "<init>", e);
//				assert(false);	// notify junit
			}
		}
	    
	    log.info("Done reading XML files, thank you for your patience! ;-)");
		return matchSets;
	}
	
	public static List<MatchSet> readDataDir(String subdirectory) throws IOException {
		return readMatches(new File(Util.getDataDir(), subdirectory));
		
	}
}
