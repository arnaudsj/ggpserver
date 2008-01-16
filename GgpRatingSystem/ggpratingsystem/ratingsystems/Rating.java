package ggpratingsystem.ratingsystems;

import ggpratingsystem.Player;

import java.util.logging.Logger;

public class Rating implements Cloneable {
	private static final Logger log = Logger.getLogger(Rating.class.getName());
	
	static {
		// inherit default level for package ggpratingsystem
		log.setLevel(null);
	}
	
	private static final double DEFAULT_RATING = 1000.0;
	private double curRating;
	private final Player player;
	
	public Rating(Player player) {
		this.player = player;
		setCurRating(DEFAULT_RATING);
	}
	
	public Rating(Player player, double initialRating) {
		this.player = player;
		setCurRating(initialRating);
	}

	public double getCurRating() {
		return curRating;
	}

	public void setCurRating(double curRating) {
		// Ratings of value 0.0 cause an error in flanagan.math.Matrix
		if (curRating == 0.0) {
			curRating = Double.MIN_VALUE;	// smallest non-zero value of double
			log.warning("Attempted to set 0.0 rating; setting " + Double.MIN_VALUE + " instead.");
		}
		
		this.curRating = curRating;
	}

	/**
	 * Resets the rating to the default values.
	 */
	public void reset() {
		setCurRating(DEFAULT_RATING);
	}

	public Player getPlayer() {
		return player;
	}

	@Override
	public Object clone() throws CloneNotSupportedException {		
		try {
			return (Rating) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}		
	}
}