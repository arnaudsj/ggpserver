package ggpratingsystem;

import static ggpratingsystem.RatingSystemType.LINEAR_REGRESSION;

import java.util.Map;
import java.util.Set;

public class LinearRegressionRating extends AbstractRating {
	private static int numMatches = 0;	// XXX

	public LinearRegressionRating(Player player) {
		super(player);
	}

	@Override
	public RatingSystemType getType() {
		return RatingSystemType.LINEAR_REGRESSION;
	}
	
	public static void updateRatings(MatchSet matches) {
		numMatches++;	// XXX
		LinearRegressionGameInfo gameInfo = (LinearRegressionGameInfo) matches
				.getGame().getGameInfo(LINEAR_REGRESSION);
		
		Map<Player, Double> actualScores = matches.overallScores();		
		Map<Player, Double> expectedScores = gameInfo.expectedScores(matches);
		
		Set<Player> players = matches.getPlayers();		
		assert(players.equals(actualScores.keySet()));
		
		/* update the player's rating accordingly */
		for (Player player : players) {
			LinearRegressionRating rating = (LinearRegressionRating) player.getRating(LINEAR_REGRESSION);
			rating.updateSingleRating(actualScores.get(player), expectedScores.get(player));
		}
	}

	/**
	 * This method adjusts the rating of a player depending on the actual and
	 * expected rewards that he received in a MatchSet. 
	 *     
	 * @param actualScore
	 * @param expectedScore
	 */
	private void updateSingleRating(double actualScore, double expectedScore) {
		// final double LEARNING_RATE = 10.0;			// XXX constant learning rate
		double LEARNING_RATE = (60 - numMatches) / 10;	// dynamic learning rate (60 is a good number, because we have 44 MatchSets and 60 > 44)
		
		double difference = actualScore - expectedScore;		
		double newRating = LEARNING_RATE * difference + getCurRating();
		
		setCurRating(newRating);
	}
}
