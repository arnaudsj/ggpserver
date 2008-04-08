package ggpratingsystem.ratingsystems;

import static ggpratingsystem.ratingsystems.RatingSystemType.CONSTANT_LINEAR_REGRESSION;

public class ConstantLinearRegressionStrategy extends AbstractLinearRegressionStrategy {
	/**
	 * This constant learning rate DOES have an influence on the outcomes. 
	 */
	private final double learningRate;

	public ConstantLinearRegressionStrategy(final double learningRate) {
		super();
		this.learningRate = learningRate;
	}

	@Override
	protected double getLearningRate() {
		return learningRate;
	}

//	@Override
	public RatingSystemType getType() {
		return CONSTANT_LINEAR_REGRESSION;
	}

	public String idString() {
		return getType().toString().toLowerCase() + "_" + Double.toString(learningRate);
	}
}
