package ggpratingsystem;

public abstract class AbstractRating {
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
		curRating = 1000.0;		
	}

	public Player getPlayer() {
		return player;
	}
}