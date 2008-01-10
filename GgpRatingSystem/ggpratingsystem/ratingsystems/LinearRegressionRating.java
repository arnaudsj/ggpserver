package ggpratingsystem.ratingsystems;

import ggpratingsystem.Player;

public class LinearRegressionRating extends AbstractRating {
	public LinearRegressionRating(Player player) {
		super(player);
	}
	
	public LinearRegressionRating(Player player, double initialRating) {
		super(player, initialRating);
	}

	
	/**
	 * This method adjusts the rating of a player depending on the actual and
	 * expected rewards that he received in a MatchSet. 
	 *     
	 * @param actualScore
	 * @param expectedScore
	 * @param learningRate 
	 */
	protected void updateSingleRating(double actualScore, double expectedScore, double learningRate)  {		
		double difference = actualScore - expectedScore;		
		double newRating = getCurRating() + learningRate * difference;
		
		setCurRating(newRating);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		return new LinearRegressionRating(getPlayer(), getCurRating());
	}
}