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

package ggpratingsystem.output;

import java.io.IOException;
import java.util.List;

import ggpratingsystem.MatchSet;
import ggpratingsystem.Player;
import ggpratingsystem.ratingsystems.Rating;
import ggpratingsystem.ratingsystems.RatingSystemType;

public class DirectOutputBuilder implements OutputBuilder {
	private final RatingsWriter writer;
	private final RatingSystemType type;

	private List<Player> players;

	private double[] nextLine;

	
	public DirectOutputBuilder(RatingsWriter writer, RatingSystemType type) {
		this.writer = writer;
		this.type = type;
	}


	
	public void initialize(List<Player> players) throws IOException {
		this.players = players;		
		/* write player names as column headings to the inner writer */
		String[] headings = new String[players.size()];
		for (int i = 0; i < headings.length; i++) {
			headings[i] = players.get(i).getName();
		}
		this.writer.println(headings);

		/* initialize nextLine with the current players' scores */
		nextLine = new double[players.size()];
		
		for (int i = 0; i < players.size(); i++) {
			double curRating = players.get(i).getRating(type).getCurRating();
			nextLine[i] = curRating;
		}
	}

	
	public void beginMatchSet(MatchSet matchSet) {
		// nothing to do
	}

	public void endMatchSet(MatchSet matchSet) {
		/* write gathered data to the inner writer */
		try {
			writer.println(nextLine);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void ratingUpdate(Rating rating) {
		/* update the player's rating */
		int position = players.indexOf(rating.getPlayer());
		if (position == -1) {
			throw new IllegalArgumentException("Unknown Player!");
		}
		nextLine[position] = rating.getCurRating(); 
	}
	
	public void finish() {
		try {
			writer.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
