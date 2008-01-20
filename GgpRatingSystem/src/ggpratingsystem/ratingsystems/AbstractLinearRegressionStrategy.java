package ggpratingsystem.ratingsystems;

import ggpratingsystem.MatchSet;
import ggpratingsystem.Player;

import java.util.Map;
import java.util.Set;

public abstract class AbstractLinearRegressionStrategy implements RatingStrategy {

	protected abstract double getLearningRate(); 
	
	@Override
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
