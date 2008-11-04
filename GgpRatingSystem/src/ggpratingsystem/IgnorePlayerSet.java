package ggpratingsystem;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class IgnorePlayerSet extends HashSet<Player> {
	private static final long serialVersionUID = -9015780749809835266L;

	public IgnorePlayerSet(File directory) throws IOException {
		CSVReader reader;
		File ignoreFile = new File(directory, "ignore_players.csv");
		
		if (ignoreFile.canRead()) {
			reader = new CSVReader(new FileReader(ignoreFile));
	
			List<String[]> lines = new LinkedList<String[]>();
			for (Iterator iter = reader.readAll().iterator(); iter.hasNext();) {
				String[] line = (String[]) iter.next();
				lines.add(line);
			}		

		    for (String[] line : lines) {
		    	if (line.length != 1) {
		    		throw new  IllegalArgumentException("Incorrect number of arguments in CSV file " + ignoreFile);
		    	}
		    	
		    	String playerName = line[0];	    	
		    	add(Player.getInstance(playerName));
			}
	    }
	}
}
