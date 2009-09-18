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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

public class IgnorePlayerSet extends HashSet<Player> {
	private static final long serialVersionUID = -9015780749809835266L;

	public IgnorePlayerSet(File directory, PlayerSet playerSet) throws IOException {
		CSVReader reader;
		File ignoreFile = new File(directory, "ignore_players.csv");
		
		if (ignoreFile.canRead()) {
			reader = new CSVReader(new FileReader(ignoreFile));
	
			List<String[]> lines = new LinkedList<String[]>();
			for (Iterator<?> iter = reader.readAll().iterator(); iter.hasNext();) {
				String[] line = (String[]) iter.next();
				lines.add(line);
			}		

		    for (String[] line : lines) {
		    	if (line.length != 1) {
		    		throw new  IllegalArgumentException("Incorrect number of arguments in CSV file " + ignoreFile);
		    	}
		    	
		    	String playerName = line[0];	    	
		    	add(playerSet.getPlayer(playerName));
			}
	    }
	}
}
