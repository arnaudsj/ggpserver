package ggpratingsystem.ratingsystems;

import static ggpratingsystem.ratingsystems.RatingSystemType.DYNAMIC_LINEAR_REGRESSION;

import ggpratingsystem.MatchSet;

import java.util.logging.Logger;

/**
 * This class is responsible for scheduling the updates to GameInfos and Ratings
 * following notification of a new MatchSet. That is, this class controls *when*
 * and *in what order* GameInfos and Ratings are updated, while delegating the
 * updates themselves to the corresponding classes.
 * 
 * @author martin
 * 
 */
public class DynamicLinearRegressionStrategy extends AbstractLinearRegressionStrategy {
	private static final Logger log = Logger.getLogger(DynamicLinearRegressionStrategy.class.getName());
	
	/*
	 * This constant multiplicative factor should not have too much of an
	 * influence on the relative outcomes. Its primary purpose is to scale the
	 * player ratings roughly between 0 and 2000 for cosmetic reasons.
	 * 
	 */ 
	private static final double LEARNING_FACTOR = 0.05;	
	
	private final int maxMatchSets;
	private int numMatchSets = 0;
	
	private double learningRate = 0.0;

	public DynamicLinearRegressionStrategy(final int maxMatchSets) {
		super();
		this.maxMatchSets = maxMatchSets;
	}

	@Override
	public RatingSystemType getType() {
		return DYNAMIC_LINEAR_REGRESSION;
	}
	
	@Override
	public void update(MatchSet matches) {
		if (maxMatchSets > numMatchSets) {
			numMatchSets++;
		} else {
			log.warning("numMatchSets reached maxMatchSets limit (" + maxMatchSets + "), not increasing numMatchSets any further!");
		}
		
		learningRate = (maxMatchSets - numMatchSets) * LEARNING_FACTOR;	// dynamic learning rate 
		
		super.update(matches);
	}

	@Override
	protected double getLearningRate() {
		assert(learningRate != 0.0);
		
		return learningRate;
	}
	
	public String idString() {
		return getType().toString().toLowerCase() + "_" + maxMatchSets;
	}
}
