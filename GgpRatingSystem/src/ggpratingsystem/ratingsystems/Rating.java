/*
    Copyright (C) 2008,2009 Martin Günther <mintar@gmx.de>

    This file is part of GgpRatingSystem.

    GgpRatingSystem is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GgpRatingSystem is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GgpRatingSystem.  If not, see <http://www.gnu.org/licenses/>.
*/

package ggpratingsystem.ratingsystems;

import ggpratingsystem.Player;

import java.util.logging.Logger;

/**
 * This class implements the rating of a player.
 * 
 * @author martin
 * 
 */
public class Rating implements Cloneable, Comparable<Rating> {
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
			log.fine("Attempted to set 0.0 rating; setting " + Double.MIN_VALUE + " instead.");
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
	
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((player == null) ? 0 : player.hashCode());
		result = PRIME * result + new Double(curRating).hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Rating other = (Rating) obj;
		if (player == null) {
			if (other.player != null)
				return false;
		} else if (!player.equals(other.player))
			return false;
		if (curRating != other.curRating)
			return false;
		return true;
	}

	
	public int compareTo(Rating otherRating) {
		int curRatingCompared = Double.compare(curRating, otherRating.curRating);
		if (curRatingCompared == 0) {
			return player.compareTo(otherRating.player);
		} else {
			return curRatingCompared;
		}
	}
}