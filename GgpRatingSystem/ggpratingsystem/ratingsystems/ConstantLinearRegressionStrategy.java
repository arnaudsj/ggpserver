package ggpratingsystem.ratingsystems;

import static ggpratingsystem.ratingsystems.RatingSystemType.CONSTANT_LINEAR_REGRESSION;

public class ConstantLinearRegressionStrategy extends AbstractLinearRegressionStrategy {
	private final double LEARNING_RATE = 10.0;			// constant learning rate

	@Override
	protected double getLearningRate() {
		return LEARNING_RATE;
	}

	@Override
	public RatingSystemType getType() {
		return CONSTANT_LINEAR_REGRESSION;
	}
}
