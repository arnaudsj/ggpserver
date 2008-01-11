package ggpratingsystem.ratingsystems;

import static ggpratingsystem.ratingsystems.RatingSystemType.CONSTANT_LINEAR_REGRESSION;
import static ggpratingsystem.ratingsystems.RatingSystemType.DYNAMIC_LINEAR_REGRESSION;
import ggpratingsystem.Game;

public class GameInfoFactory {
	
	/**
	 * SuppressWarnings("deprecation") is added, since this is the only place
	 * where the constructors of the GameInfo hierarchy should be used, and so
	 * it is okay to do just that.
	 */
	@SuppressWarnings("deprecation")
	public static AbstractGameInfo makeGameInfo(RatingSystemType type, Game game) {
		AbstractGameInfo result;
		switch (type) {
		case DYNAMIC_LINEAR_REGRESSION:
			result = new LinearRegressionGameInfo(DYNAMIC_LINEAR_REGRESSION, game);
			break;
			
		case CONSTANT_LINEAR_REGRESSION:
			result = new LinearRegressionGameInfo(CONSTANT_LINEAR_REGRESSION, game);
			break;
			
		/* ****************** ADD NEW RATING SYSTEMS HERE ****************** */
			
		default:
			throw new IllegalArgumentException("unknown RatingSystemType!");
		}
		
		return result;
	}
}
