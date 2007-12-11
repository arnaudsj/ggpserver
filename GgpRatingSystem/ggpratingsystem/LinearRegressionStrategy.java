package ggpratingsystem;

import flanagan.analysis.Regression;

public class LinearRegressionStrategy extends RatingStrategy {
	private static RatingStrategy instance = new LinearRegressionStrategy();

	@Override
	public RatingType getType() {
		return RatingType.LINEAR_REGRESSION;
	}

	@Override
	public RatingStrategy getInstance() {
		return instance;
	}

	/* (non-Javadoc)
	 * @see ggpratingsystem.RatingStrategy#updateSkills(ggpratingsystem.MatchSet)
	 */
	@Override
	public void updateSkills(MatchSet matches) {
		int numPlayers = matches.getGame().getRoles().size();
		int numMatches = matches.getMatches().size();
		
		double[][] xdata = new double[numPlayers][numMatches] ;
		double[] ydata = new double[numMatches];
		
		/* one linear regression for each player */
		for (int i = 0; i < numPlayers; i++) {
			
			for (int j = 0; j < numMatches; j++) {
				Match match = matches.getMatches().get(j);
				
				/* the target variable is the score of the current player */
				ydata[j] = match.getScores().get(i);
				
				/* the source variables are the ratings of all players in the match */
				for (int k = 0; k < numPlayers; k++) {
					Player player = match.getPlayers().get(k);
					xdata[k][j] = player.getRating(RatingType.LINEAR_REGRESSION).getCurRating();
				}
			}
			Regression reg = new Regression(xdata, ydata);
			reg.linear();
			double [] coefficients = reg.getCoeff();
			
			/*
			 * TODO: if coefficient of current player (k == i) < 0, re-run the
			 * regression without that player and set that coefficient to 0.
			 * this is necessary because otherwise players with a low rating are
			 * expected to score higher than players with a high rating and
			 * punished accordingly.
			 */
		}
	}
}
