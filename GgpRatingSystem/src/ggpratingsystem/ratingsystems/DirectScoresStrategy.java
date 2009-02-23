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

import static ggpratingsystem.ratingsystems.RatingSystemType.DIRECT_SCORES;

import java.util.Map;
import java.util.Map.Entry;

import ggpratingsystem.MatchSet;
import ggpratingsystem.Player;

/**
 * This is not really a rating strategy, it just sums up the scores rewarded to
 * each player, in order to make the ratings comparable to the received scores.
 * 
 * @author martin
 * 
 */
public class DirectScoresStrategy implements RatingStrategy {

	public RatingSystemType getType() {
		return DIRECT_SCORES;
	}

	public String idString() {
		return getType().toString().toLowerCase();
	}

	public void update(MatchSet matches) {
		Map<Player, Double> overallScores = matches.overallScores();
		
		for (Entry<Player, Double> entry : overallScores.entrySet()) {
			Player player = entry.getKey();
			Double receivedScore = entry.getValue();

			Rating rating = player.getRating(getType());
			double curRating = rating.getCurRating();
			
			receivedScore = receivedScore * getRoundMultiplicator(matches.getRound());
			
			rating.setCurRating(curRating + receivedScore);
		}
		
		
	}
		
	// TODO: Parametrize round multiplicators
	private double getRoundMultiplicator(int round) {
		switch (round) {
		case 1:
			return 0.25;

		case 2:
			return 0.5;

		case 3:
			return 0.5;

		default:
			return 1.0;
		}
	}
}
