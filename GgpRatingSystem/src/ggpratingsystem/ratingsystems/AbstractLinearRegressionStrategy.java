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

import ggpratingsystem.MatchSet;
import ggpratingsystem.Player;

import java.util.Map;
import java.util.Set;

public abstract class AbstractLinearRegressionStrategy implements RatingStrategy {

	protected abstract double getLearningRate(); 
	
	public void update(MatchSet matches) {
		LinearRegressionGameInfo gameInfo = (LinearRegressionGameInfo) matches
				.getGame().getGameInfo(this.getType());
		
		gameInfo.updateGameInfo(matches);
		
		Map<Player, Double> actualScores = matches.overallScores();		
		Map<Player, Double> expectedScores = gameInfo.expectedScores(matches);
		
		Set<Player> players = matches.getPlayers();		
		assert(players.equals(actualScores.keySet()));
		assert(players.equals(expectedScores.keySet()));
		
		/* update the players' ratings */
		for (Player player : players) {
			LinearRegressionRating rating = (LinearRegressionRating) player.getRating(this.getType());
			rating.updateSingleRating(actualScores.get(player), expectedScores.get(player), this.getLearningRate());
		}
	}
}
