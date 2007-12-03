package ggpratingsystem;

public abstract class Rating {
	double curRating = 1000.0;
	
	public abstract RatingType getType();
	
	public double getCurRating() {
		return curRating;
	}

	public void setCurRating(double curRating) {
		this.curRating = curRating;
	}
	
}