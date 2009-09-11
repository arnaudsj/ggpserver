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

package ggpratingsystem.ratingsystems;

import ggpratingsystem.Player;
import ggpratingsystem.PlayerSet;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import au.com.bytecode.opencsv.CSVReader;

public class RatingFactory {
//	private static HashMap<String, Double> initialRatings = new HashMap<String, Double>();
//	
//	static {
//		initialRatings.put("U-TEXAS-LARG", 1149.521816533276);
//		initialRatings.put("CLUNEPLAYER", 1894.2681939518773);
//		initialRatings.put("JIGSAWBOT", 761.3162874298564);
//		initialRatings.put("FLUXPLAYER", 1567.9864873802828);
//		initialRatings.put("O-GRABME", 962.5372358694761);
//		initialRatings.put("LUCKY-LEMMING", 637.308902572719);
//		initialRatings.put("RANDOM4", 1000.0);
//		initialRatings.put("W-WOLFE", -689.5230242719155);
//		initialRatings.put("CADIA-PLAYER", 2354.223947158014);
//		initialRatings.put("ARY", 1687.9994202988808);
//		initialRatings.put("RANDOM3", 1031.875);
//		initialRatings.put("RANDOM2", 936.25);
//		initialRatings.put("RANDOM", 615.7172571053981);
//		initialRatings.put("THE-PIRATE", 380.38194444444446);
//	}
	
	
	public static Map<RatingSystemType, List<Rating>> initialRatings = new HashMap<RatingSystemType, List<Rating>>();
	
	public static Rating makeRating(RatingSystemType type, Player player) {
		Rating result;
		
//		double initialRating;
//		if (initialRatings.containsKey(player.getName())) {
//			initialRating = initialRatings.get(player.getName());
//		}
//		else {
//			initialRating = 1000.0;
//		}
	
		switch (type) {
		case DYNAMIC_LINEAR_REGRESSION:
//			result = new LinearRegressionRating(player, initialRating);
			result = new LinearRegressionRating(player);
			break;
			
		case CONSTANT_LINEAR_REGRESSION:
//			result = new LinearRegressionRating(player, initialRating);
			result = new LinearRegressionRating(player);
			break;
			
		case DIRECT_SCORES:
			result = new Rating(player, 0.0);
			break;
			
			/* ****************** ADD NEW RATING SYSTEMS HERE ****************** */
			
		default:
			throw new IllegalArgumentException("unknown RatingSystemType: " + type);
		}
		
		return result;
	}

	public static void initializeRatings(RatingSystemType type, File previousRatings, PlayerSet playerSet) throws IOException, RatingException {
		CSVReader reader;
		reader = new CSVReader(new FileReader(previousRatings));

		// first line: player names
		String[] playerNames = reader.readNext();
		
		// last line: final ratings of previous competition == initial ratings of this competition
		String[] ratings = null;
		for (String [] next = reader.readNext(); next != null; next = reader.readNext()) {
			ratings = next;
		}
		
		if (playerNames == null || ratings == null || playerNames.length != ratings.length) {
			throw new RatingException("Wrong format of previous CSV output file.");
		}
		
		// set and store initial ratings
		for (int i = 0; i < playerNames.length; i++) {
			String playerName = playerNames[i];
	    	double curRating = Double.parseDouble(ratings[i]);
			Rating rating = playerSet.getPlayer(playerName).getRating(type);
			rating.setCurRating(curRating);
			try {
				initialRatings(type).add((Rating) rating.clone());
			} catch (CloneNotSupportedException e) {
				throw new InternalError("Rating not cloneable!");
			}		
			
		}
	}

	private static List<Rating> initialRatings(RatingSystemType type) {
		List<Rating> result = initialRatings.get(type);
		if (result == null) {
			result = new LinkedList<Rating>();
			initialRatings.put(type, result);
		}
		return result;
	}
	
	public static List<Rating> getInitialRatings(RatingSystemType type) {
		return new LinkedList<Rating>(initialRatings(type));
	}
}
