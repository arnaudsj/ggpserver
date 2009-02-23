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

import static ggpratingsystem.ratingsystems.RatingSystemType.CONSTANT_LINEAR_REGRESSION;

public class ConstantLinearRegressionStrategy extends AbstractLinearRegressionStrategy {
	/**
	 * This constant learning rate DOES have an influence on the outcomes. 
	 */
	private final double learningRate;

	public ConstantLinearRegressionStrategy(final double learningRate) {
		super();
		this.learningRate = learningRate;
	}

	@Override
	protected double getLearningRate() {
		return learningRate;
	}

//	@Override
	public RatingSystemType getType() {
		return CONSTANT_LINEAR_REGRESSION;
	}

	public String idString() {
		return getType().toString().toLowerCase() + "_" + Double.toString(learningRate);
	}
}
