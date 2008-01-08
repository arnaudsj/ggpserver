package ggpratingsystem;

public abstract class AbstractRating {
	private static final double DEFAULT_RATING = 1000.0;	// This must NOT be 0.0! (Otherwise error in flanagan.math.Matrix)
	private double curRating;
	private final Player player;
	
	public AbstractRating(Player player) {
		this.player = player;
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
		curRating = DEFAULT_RATING;		
	}

	public Player getPlayer() {
		return player;
	}
}