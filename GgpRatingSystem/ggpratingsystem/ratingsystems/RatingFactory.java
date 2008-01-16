package ggpratingsystem.ratingsystems;

import ggpratingsystem.Player;

public class RatingFactory {
	public static Rating makeRating(RatingSystemType type, Player player) {
		Rating result;
		
		switch (type) {
		case DYNAMIC_LINEAR_REGRESSION:
			result = new LinearRegressionRating(player);
			break;
			
		case CONSTANT_LINEAR_REGRESSION:
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
}
