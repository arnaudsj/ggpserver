package ggpratingsystem;

public class LinearRegressionRating extends Rating {

	@Override
	public RatingType getType() {
		return RatingType.LINEAR_REGRESSION;
	}

}
