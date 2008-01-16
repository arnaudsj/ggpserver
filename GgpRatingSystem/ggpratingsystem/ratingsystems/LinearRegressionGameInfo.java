package ggpratingsystem.ratingsystems;

import static java.util.logging.Level.FINE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import flanagan.analysis.Regression;
import ggpratingsystem.Game;
import ggpratingsystem.Match;
import ggpratingsystem.MatchSet;
import ggpratingsystem.Player;

/**
 * @author martin
 *
 */
public class LinearRegressionGameInfo extends AbstractGameInfo {
	public static final double DEFAULT_EXPECTED_SCORE = 50.0;
	
	private static final Logger log = Logger.getLogger(LinearRegressionGameInfo.class.getName());
	
	static {
		// inherit default level for package ggpratingsystem
		log.setLevel(null);
	}

	private double coeffs[][];
		// first dimension: target player
		// second dimension: coefficients (index 0 is y-axis intercept, index n is player n-1)
		// example: 
		//   coeffs[2][0] is target player 2, intercept
		//   coeffs[0][3] is target player 0, coefficient for player 2

	private final int numPlayers;
	private final RatingSystemType ratingSystemType;
	
	private int numMatches;
	
	/**
     * @deprecated Use {@link ggpratingsystem.ratingsystems.GameInfoFactory#makeGameInfo(RatingSystemType, Game)} instead.
	 */
	@Deprecated
	protected LinearRegressionGameInfo(RatingSystemType ratingSystemType, Game game) {
		super(game);
		numPlayers = game.getRoles().size();
		this.ratingSystemType = ratingSystemType;
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
		return ratingSystemType;
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
			
			/* extract ratings */
			double[] ratings = new double[numPlayers];
			for (int j = 0; j < numPlayers; j++) {
				ratings[j] = players.get(j).getRating(ratingSystemType).getCurRating();
			}
			
			for (int i = 0; i < numPlayers; i++) {
				Player player = players.get(i);
				
				Double expectedScore = expectedScores.get(player);
				if (expectedScore == null) {
					expectedScore = 0.0;
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
	 * y = c[0] + c[1]*x[0] + c[2]*x[1] +c[3]*x[2] + . . .
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
//			assert (ratings[i] > 0);
			// This had to be uncommented; the ratings CAN become negative!
			
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
	
	
	private double[] calcCoefficients(MatchSet matches, int targetRole) {
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
	 * Calculates an array coefficients[numRoles], where numRoles is the number 
	 * of roles in the game.
	 *    coefficients[0] = y-axis intercept
	 *    coefficients[n + 1] = coefficient of role n
	 * 
	 * If the flag zeroTargetCoeff is set, then it will be ensured that
	 *    coefficients[targetRole] = 0.0
	 *    
	 * The reason for this is that sometimes, coefficients[targetRole] will be
	 * negative, which is not desirable. In this case, calcCoefficientsInner
	 * can be called again with zeroTargetCoeff = true.  
	 * 
	 * @param matches
	 * @param targetRole       role number for which to calculate the coefficients
	 * @param zeroTargetCoeff  force the coefficient of the target player to be 0
	 * @return
	 */
	private double[] calcCoefficientsInner(MatchSet matches, int targetRole, boolean zeroTargetCoeff) {
		int numRoles = matches.getGame().getRoles().size();
		int numMatches = matches.getMatches().size();
		
		double[] coefficients = new double[numRoles + 1];

		/* Number of non-ignored players must be at least 1 to perform a linear regression */
        if (zeroTargetCoeff && (numRoles == 1)) {
			/* fallback */
//			coefficients[0] = matches.averageScorePerMatch();
			coefficients[0] = matches.averageRoleScore().get(targetRole);
			
			return coefficients;
		}
        
        /*
		 * Prepare the arrays xdata and ydata: inputs to the linear regression
		 * algorithm. ydata holds the target variable, xdata holds the source
		 * variables (see below).
		 */
		double[][] xdata;
		double[]   ydata = new double[numMatches];

		if (zeroTargetCoeff) {
			// role targetRole will not be written, so we need one column less in the array 
			xdata = new double[numRoles - 1][numMatches] ; 
		} else {
			xdata = new double[numRoles][numMatches] ;
		}
		
		/* copy the match data into the xdata/ydata arrays */
		for (int matchNumber = 0; matchNumber < numMatches; matchNumber++) {
			Match match = matches.getMatches().get(matchNumber);
			
			/*
			 * the target variable (the value that the linear regression is
			 * trying to predict) is the score of the target role
			 */
			ydata[matchNumber] = match.getScores().get(targetRole);
			
			/*
			 * the source variables (the values used by the linear regression in
			 * its prediction) are the ratings of all players in the match, in the
			 * order of the roles that they played
			 */
			int roleToRead = 0;
			int roleToWrite = 0;
			while (true) {
				if (zeroTargetCoeff && roleToRead == targetRole) {					
					// skip this role, don't include it in the xdata array
					roleToRead++;
				}
				
				if (roleToRead >= numRoles) {
					break;
				}
				
				Player player = match.getPlayers().get(roleToRead);
				xdata[roleToWrite][matchNumber] = player.getRating(ratingSystemType).getCurRating();

				roleToWrite++;
				roleToRead++;
			}
		}
		
		/*
		 * Degrees of freedom must be at least 1 to perform a linear regression
		 * (i.e., you have to have enough matches relative to the number of
		 * source variables (the number of players))
		 */
        int degreesOfFreedom = ydata.length - (xdata.length + 1);
        if (degreesOfFreedom  < 1) {
			/* fallback */
//			coefficients[0] = matches.averageScorePerMatch();
			coefficients[0] = matches.averageRoleScore().get(targetRole);
			
			return coefficients;
		}
        
		/* Everything seems to be okay, now we can perform the linear regression */		
		Regression reg = new Regression(xdata, ydata);
		reg.linear();
		
		/*
		 * If the debug level at least "FINE", output some debug info, but only
		 * the first time this function is called, not when it is called the
		 * second time with zeroTargetCoeff == true.
		 */
		if (log.isLoggable(FINE) && !zeroTargetCoeff) {
			String filename = "output_" + ratingSystemType + "_" + matches.toString() + "_role_" + targetRole + "_run_.txt";	// the number after "run_" will be written by reg.print()
			filename = filename.toLowerCase();
			log.fine("Writing regression debug output to file " + filename + ". " +
					"If you don't want this, set the log level higher than FINE.");
			reg.print(filename);
		}

		
		double[] tempCoeff = reg.getCoeff();

		/* If all roles were included in the regression, we are done */
		if (!zeroTargetCoeff) {
			return tempCoeff;
		}
		
		/*
		 * Otherwise (if a player was skipped), we have to adjust the
		 * coefficients again: the value for the removed player (which is zero)
		 * must be re-inserted into the coefficients
		 */
		int roleToRead = 0;
		int roleToWrite = 0;
		while (roleToWrite < numRoles) {
			if (roleToRead == targetRole + 1) { // targetRole + 1, because
												// tempCoeff[0] is the y-axis
												// intercept --> everything
												// shifts by 1 to the right
				// skip writing (leave value at 0.0)
				roleToWrite++;
			}
			
			coefficients[roleToWrite] = tempCoeff[roleToRead];

			roleToWrite++;
			roleToRead++;
		}
		return coefficients;
	}
}
