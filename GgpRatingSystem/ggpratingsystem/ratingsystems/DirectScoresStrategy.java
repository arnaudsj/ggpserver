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

		case 4:
			return 1.0;

		default:
			throw new IllegalArgumentException();
		}
	}
}
