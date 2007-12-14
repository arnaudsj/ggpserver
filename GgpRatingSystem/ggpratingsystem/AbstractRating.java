package ggpratingsystem;

public abstract class AbstractRating {
	private double curRating;
	
	public AbstractRating() {
		super();
		reset();
	}

	public abstract RatingSystemType getType();
	
	public double getCurRating() {
		return curRating;
	}

	public void setCurRating(double curRating) {
		this.curRating = curRating;
	}

	/**
	 * Resets the rating to the default values.
	 */
	public void reset() {
		curRating = 1000.0;		
	}
}