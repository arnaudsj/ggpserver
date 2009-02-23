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

public class LinearRegressionRating extends Rating {
	public LinearRegressionRating(Player player) {
		super(player);
	}
	
	public LinearRegressionRating(Player player, double initialRating) {
		super(player, initialRating);
	}

	
	/**
	 * This method adjusts the rating of a player depending on the actual and
	 * expected rewards that he received in a MatchSet. 
	 *     
	 * @param actualScore
	 * @param expectedScore
	 * @param learningRate 
	 */
	protected void updateSingleRating(double actualScore, double expectedScore, double learningRate)  {		
		double difference = actualScore - expectedScore;		
		double newRating = getCurRating() + learningRate * difference;
		
		setCurRating(newRating);
	}
	
	@Override
	public Object clone() throws CloneNotSupportedException {
		try {
			return (LinearRegressionRating) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError();
		}		
	}
}