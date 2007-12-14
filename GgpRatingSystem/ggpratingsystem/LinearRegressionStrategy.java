package ggpratingsystem;

import static ggpratingsystem.RatingSystemType.LINEAR_REGRESSION;

/**
 * This class is responsible for scheduling the updates to GameInfos and Ratings
 * following notification of a new MatchSet. That is, this class controls *when*
 * and *in what order* GameInfos and Ratings are updated, while delegating the
 * updates themselves to the corresponding classes.
 * 
 * @author martin
 * 
 */
public class LinearRegressionStrategy extends AbstractRatingStrategy {
	private static LinearRegressionStrategy instance = new LinearRegressionStrategy();

	private LinearRegressionStrategy() {
		super();
	}

	@Override
	public RatingSystemType getType() {
		return LINEAR_REGRESSION;
	}

	public static LinearRegressionStrategy getInstance() {
		return instance;
	}

	@Override
	public void update(MatchSet matches) {
		AbstractGameInfo gameInfo = matches.getGame().getGameInfo(LINEAR_REGRESSION);		
		gameInfo.updateGameInfo(matches);
		
		LinearRegressionRating.updateRatings(matches);
	}	
}
