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

import static ggpratingsystem.ratingsystems.RatingSystemType.DYNAMIC_LINEAR_REGRESSION;

import ggpratingsystem.MatchSet;

import java.util.logging.Logger;

/**
 * This class is responsible for scheduling the updates to GameInfos and Ratings
 * following notification of a new MatchSet. That is, this class controls *when*
 * and *in what order* GameInfos and Ratings are updated, while delegating the
 * updates themselves to the corresponding classes.
 * 
 * @author martin
 * 
 */
public class DynamicLinearRegressionStrategy extends AbstractLinearRegressionStrategy {
	private static final Logger log = Logger.getLogger(DynamicLinearRegressionStrategy.class.getName());
	
	static {
		// inherit default level for package ggpratingsystem
		log.setLevel(null);
	}
	
	/*
	 * This constant multiplicative factor should not have too much of an
	 * influence on the relative outcomes. Its primary purpose is to scale the
	 * player ratings roughly between 0 and 2000 for cosmetic reasons.
	 * 
	 */ 
	private static final double LEARNING_FACTOR = 0.05;	
	
	private final int maxMatchSets;
	private int numMatchSets = 0;
	
	private double learningRate = 0.0;

	public DynamicLinearRegressionStrategy(final int maxMatchSets) {
		super();
		this.maxMatchSets = maxMatchSets;
	}

//	@Override
	public RatingSystemType getType() {
		return DYNAMIC_LINEAR_REGRESSION;
	}
	
	@Override
	public void update(MatchSet matches) {
		if (maxMatchSets > numMatchSets) {
			numMatchSets++;
		} else {
			log.warning("numMatchSets reached maxMatchSets limit (" + maxMatchSets + "), not increasing numMatchSets any further!");
		}
		
		learningRate = (maxMatchSets - numMatchSets) * LEARNING_FACTOR;	// dynamic learning rate 
		
		super.update(matches);
	}

	@Override
	protected double getLearningRate() {
		assert(learningRate != 0.0);
		
		return learningRate;
	}
	
	public String idString() {
		return getType().toString().toLowerCase() + "_" + maxMatchSets;
	}
}
