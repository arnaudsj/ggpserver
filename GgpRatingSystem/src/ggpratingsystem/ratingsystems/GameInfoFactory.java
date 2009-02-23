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
import static ggpratingsystem.ratingsystems.RatingSystemType.DYNAMIC_LINEAR_REGRESSION;
import ggpratingsystem.Game;

public class GameInfoFactory {
	
	/**
	 * SuppressWarnings("deprecation") is added, since this is the only place
	 * where the constructors of the GameInfo hierarchy should be used, and so
	 * it is okay to do just that.
	 */
	@SuppressWarnings("deprecation")
	public static AbstractGameInfo makeGameInfo(RatingSystemType type, Game game) {
		AbstractGameInfo result;
		switch (type) {
		case DYNAMIC_LINEAR_REGRESSION:
			result = new LinearRegressionGameInfo(DYNAMIC_LINEAR_REGRESSION, game);
			break;
			
		case CONSTANT_LINEAR_REGRESSION:
			result = new LinearRegressionGameInfo(CONSTANT_LINEAR_REGRESSION, game);
			break;
			
//		case DIRECT_SCORES: direct scores does not need game info
			
		/* ****************** ADD NEW RATING SYSTEMS HERE ****************** */
			
		default:
			throw new IllegalArgumentException("unknown RatingSystemType!");
		}
		
		return result;
	}
}
