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

public enum RatingSystemType {
//	ELO,
//	GLICKO,
//	GLICKO_2,
//	TRUE_SKILL,
	CONSTANT_LINEAR_REGRESSION,
	DYNAMIC_LINEAR_REGRESSION,
	DIRECT_SCORES;
	
	/*
	 * Whenever a new RatingSystemType is created, the following have to be
	 * provided for this type:
	 * 
	 * - a new class implementing RatingStrategy, defining how to update all Ratings and GameInfos for a match set
	 * 
	 * - if needed: 
	 * 		- a new subclass of Rating, defining how to update a single rating
	 * 		- a corresponding entry in RatingFactory
	 * 
	 * - if needed:
	 * 		- a new subclass of AbstractGameInfo
	 * 		- a corresponding entry in GameInfoFactory 
	 *  
	 * - a new option in CommandLineInterface, search for:
	 * 		****************** ADD NEW RATING SYSTEMS HERE ******************
	 * 
	 * - parse the new option in CommandLineInterface.main()
	 */
}
