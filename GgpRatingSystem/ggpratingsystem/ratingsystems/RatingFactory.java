package ggpratingsystem.ratingsystems;

import ggpratingsystem.Player;

public class RatingFactory {
	public static AbstractRating makeRating(RatingSystemType type, Player player) {
		AbstractRating result;
		
		switch (type) {
		case DYNAMIC_LINEAR_REGRESSION:
			result = new LinearRegressionRating(player);
			break;
			
		case CONSTANT_LINEAR_REGRESSION:
			result = new LinearRegressionRating(player);
			break;
			
			/* ****************** ADD NEW RATING SYSTEMS HERE ****************** */
			
		default:
			throw new IllegalArgumentException("unknown RatingSystemType!");
		}
		
		return result;
	}
}
