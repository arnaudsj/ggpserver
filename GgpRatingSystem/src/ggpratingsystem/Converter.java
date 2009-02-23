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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import au.com.bytecode.opencsv.CSVReader;

/**
 * This class is really only a quick hack for the 2006 competition matches. It converts 
 * a .csv file containing all the data into a set of XML files.
 * 
 * @author martin
 */
public class Converter {
	private static final Logger log = Logger.getLogger(Converter.class.getName());
	
	static {
		// inherit default level for package ggpratingsystem
		log.setLevel(null);
	}

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		log.info("Reading matches.csv.");
		File directory = new File("/home/martin/Desktop/tmp");
		
		File matchesFile = new File(directory, "rawdata.csv");
		CSVReader reader = new CSVReader(new FileReader(matchesFile));

		List<String[]> lines = new LinkedList<String[]>();
		for (Iterator iter = reader.readAll().iterator(); iter.hasNext();) {
			String[] line = (String[]) iter.next();
			lines.add(line);
		}
		
	    for (String[] line : lines) {   // "Matchid" , "Player1;Player2;...", "Game", "Reward1;Reward2;..."
	    	if (line.length != 4) {
	    		throw new  IllegalArgumentException("Incorrect number of arguments in CSV file " + matchesFile);
	    	}
	    	
	    	String matchID = line[0];	    	
	    	String players = line[1];
//	    	String game    = line[2];
	    	String rewards = line[3];
	    	
	    	StringTokenizer playerTokens = new StringTokenizer(players, ";");
	    	StringTokenizer rewardTokens = new StringTokenizer(rewards, ";");
	    	int numPlayers = playerTokens.countTokens();
	    	assert(rewardTokens.countTokens() == numPlayers);
	    	
	    	
//	    	Writer output = new PrintWriter(System.out);
	    	Writer output = new FileWriter(new File(directory, matchID + ".xml"));
	    	
	    	output.write("<?xml version=\"1.0\"?>\n");
	    	output.write("<!DOCTYPE match SYSTEM \"http://games.stanford.edu/gamemaster/xml/viewmatch.dtd\">\n");
	    	output.write("<match>\n");

	    	// --- match id ---
	    	output.write("\t<match-id>" + matchID + "</match-id>\n");
	    	
	    	// --- roles ---
	    	for (int i = 1; i <= numPlayers; i++) {
	    		output.write("\t<role>role" + i + "</role>\n");				
			}
	    	
	    	// --- players ---
	    	while (playerTokens.hasMoreTokens()) {
	    		output.write("\t<player>" + playerTokens.nextToken() + "</player>\n");
			}

	    	// --- scores ---
	    	output.write("\t<scores>\n");
	    	while (rewardTokens.hasMoreTokens()) {
	    		output.write("\t\t<reward>" + rewardTokens.nextToken() + "</reward>\n");
			}
	    	output.write("\t</scores>\n");

	    	output.write("</match>\n");
	    	output.close();   // flush instead if PrintWriter 
	    }
	}
}
