package ggpratingsystem;

import static ggpratingsystem.RatingSystemType.LINEAR_REGRESSION;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import flanagan.analysis.Regression;

/**
 * @author martin
 *
 */
public class LinearRegressionGameInfo extends AbstractGameInfo {
	private static final Logger log = Logger.getLogger(LinearRegressionGameInfo.class.getName());
	
	public static final double DEFAULT_EXPECTED_SCORE = 50.0;
	
	private double coeffs[][];
		// first dimension: target player
		// second dimension: coefficients (index 0 is y-axis intercept, index n is player n-1)
		// example: 
		//   coeffs[2][0] is target player 2, intercept
		//   coeffs[0][3] is target player 0, coefficient for player 2

	private int numMatches;
	private final int numPlayers;
	
	public LinearRegressionGameInfo(Game game) {
		super(game);
		numPlayers = game.getRoles().size();
		reset();
	}
	
	
	@Override
	public void reset() {
		coeffs = new double[numPlayers][numPlayers + 1];
		
		/*
		 * Initialize coefficients so that if expectedScore() is called before
		 * updateGameInfo() is ever called, DEFAULT_EXPECTED_SCORE will be
		 * returned for all players.
		 */		
		for (int i = 0; i < numPlayers; i++) {
			coeffs[i][0] = DEFAULT_EXPECTED_SCORE;
		}
		
		numMatches = 0;
	}

	
	@Override
	public RatingSystemType getType() {
		return RatingSystemType.LINEAR_REGRESSION;
	}

	
	@Override
	public void updateGameInfo(MatchSet matches) {
		if (!matches.getGame().equals(this.getGame())) {
			throw new IllegalArgumentException("Wrong game for this GameInfo!");
		}
		
		int newNumMatches = matches.getMatches().size();
		
		double[][] coefficients = new double[numPlayers][numPlayers + 1];
		
		/* one linear regression for each player */
		for (int i = 0; i < numPlayers; i++) {
			coefficients[i] = calcCoefficients(matches, i);
		}
			
		updateCoefficients(coefficients, newNumMatches);
	}
	
	/**
	 * @param matches
	 * @return overall (sum) expected score for all players in the given match
	 *         set, based on the current coefficients
	 */
	public Map<Player, Double> expectedScores(MatchSet matches) {
		if (!matches.getGame().equals(this.getGame())) {
			throw new IllegalArgumentException("Wrong game for this GameInfo!");
		}
		
		Map<Player, Double> expectedScores = new HashMap<Player, Double>();
		
		List<Match> matchList = matches.getMatches();
		
		for (Match match : matchList) {
			List<Player> players = match.getPlayers();
			
			for (int i = 0; i < numPlayers; i++) {
				Player player = players.get(i);
				
				Double expectedScore = expectedScores.get(player);
				if (expectedScore == null) {
					expectedScore = 0.0;
				}
				
				/* extract ratings */
				double[] ratings = new double[numPlayers];
				for (int j = 0; j < numPlayers; j++) {
					ratings[j] = players.get(j).getRating(LINEAR_REGRESSION).getCurRating();
				}
				
				expectedScore += multiplyRatingsCoefficients(i, ratings);
				expectedScores.put(player, expectedScore);
			}
		}
		
		log.info(expectedScores.toString());

		return expectedScores;		
	}

	
	/**
	 * Calculates the y value for the following linear regression with
	 * intercept:
	 * 
	 * y = c[0] + c[1]*x[0] + c[2]*x[1] +c[3]*x[3] + . . .
	 * 
	 * where c[i] = coeffs[targetRole][i]
	 * and   x[j] = rating of the player playing role j.
	 * 
	 * @param targetRole
	 *            number of the role for which to calculate the y value
	 *            (expected score).
	 * @param ratings
	 *            the ratings of the other players, in the order of their played
	 *            roles.
	 * @return the expected score of role *targetRole*, given the *ratings* and
	 *         the current coefficients *coeffs*. Bounded between 0 and 100.
	 */
	private double multiplyRatingsCoefficients(int targetRole, double[] ratings) {
		assert (0 <= targetRole && targetRole < numPlayers);
		assert (ratings.length == numPlayers);
		
		double result = coeffs[targetRole][0];	// intercept
		
		for (int i = 0; i < numPlayers; i++) {
			assert (ratings[i] > 0);
			
			result += coeffs[targetRole][i + 1] * ratings[i];
		}
		
		if (result < 0.0) {
			result = 0.0;
		}
		else if (result > 100.0) {
			result = 100.0;
		}
		
		return result;
	}

	
	private void updateCoefficients(double[][] newCoeffs, int newNumMatches) {
		if (newCoeffs.length != numPlayers) {
			throw new IllegalArgumentException("wrong array size");
		}
		
		double[][] updatedCoeffs = new double[numPlayers][numPlayers + 1];

		/*
		 * calculate the weighted average; I don't know if this is valid or if
		 * one should rather re-calculate the whole linear regression with ALL
		 * matches (this should be safer).
		 */
		for (int i = 0; i < numPlayers; i++) {
			if (newCoeffs[i].length != numPlayers + 1) {
				throw new IllegalArgumentException("wrong array size");
			}
			
			for (int j = 0; j < numPlayers + 1; j++) {
				updatedCoeffs[i][j] = 
					(numMatches * coeffs[i][j] + newNumMatches * newCoeffs[i][j])
						/ (numMatches + newNumMatches);
			}
		}
		
		coeffs = updatedCoeffs;
		numMatches = numMatches + newNumMatches;
	}
	
	
	private static double[] calcCoefficients(MatchSet matches, int targetRole) {
		double[] result;
		
		/* calculate coefficients for all players */
		result = calcCoefficientsInner(matches, targetRole, false);
		
		/*
		 * if coefficient of current player < 0, re-run the
		 * regression without that player and set that coefficient to 0.
		 * this is necessary because otherwise players with a low rating are
		 * expected to score higher than players with a high rating and
		 * punished accordingly.
		 */
		if (result[targetRole] < 0) {
			result = calcCoefficientsInner(matches, targetRole, true);
		}

		return result;
	}

	
	/**
	 * @param matches
	 * @param targetRole       role number for which to calculate the coefficients
	 * @param zeroTargetCoeff  force the coefficient of the target player to be 0
	 * @return
	 */
	private static double[] calcCoefficientsInner(MatchSet matches, int targetRole, boolean zeroTargetCoeff) {
		int numPlayers = matches.getGame().getRoles().size();
		int numMatches = matches.getMatches().size();
		
		double[][] xdata = new double[numPlayers][numMatches] ;;
		double[]   ydata = new double[numMatches];
		
		for (int i = 0; i < numMatches; i++) {
			Match match = matches.getMatches().get(i);
			
			/* the target variable is the score of the current player */
			ydata[i] = match.getScores().get(targetRole);
			
			/* the source variables are the ratings of all players in the match */
			for (int j = 0; j < numPlayers; j++) {
				if (zeroTargetCoeff && j == targetRole) {
					// By setting the player's ranking to 0, the target player's coefficient
					// will not be included in the linear regression
					xdata[j][i] = 0.0;

					/*
					 * FIXME This does not work; targetRole has to be removed completely
					 * (otherwise the Regression class complains about a singular Matrix)
					 */
				}
				else {
					Player player = match.getPlayers().get(j);
					xdata[j][i] = player.getRating(LINEAR_REGRESSION).getCurRating();
				}
			}
		}
		
		
		/* Degrees of freedom must be at least 1 */
        int degreesOfFreedom = ydata.length - (xdata.length + 1);
		
        if ((degreesOfFreedom  < 1) || (zeroTargetCoeff && (numPlayers == 1))) {	// TODO: number of non-ignored players must also be at least 1 
			/* Not enough matches for a linear regression --> fallback: average score */
			double[] coefficients = new double[numPlayers + 1];
			coefficients[0] = matches.averageScorePerMatch();
			
			return coefficients;			
		} else {
			/* Perform multiple linear regression on the data */
			
			Regression reg = new Regression(xdata, ydata);
			reg.linear();
			double[] coefficients = reg.getCoeff();
	
			/*
			 * when the target player's ranking is set to zero, the linear
			 * regression may return an arbitrary coefficient for the target player,
			 * so the coefficient also has to be forced to zero afterwards.
			 */
			if (zeroTargetCoeff) {
				coefficients[targetRole + 1] = 0.0;
				// the y-axis intercept is at coefficients[0], so the coefficient
				// for player n is at n + 1
			}
			
			return coefficients;
		}
	}
}
