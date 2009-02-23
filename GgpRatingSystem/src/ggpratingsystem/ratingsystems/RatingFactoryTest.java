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

import junit.framework.TestCase;

public class RatingFactoryTest extends TestCase {

	public void testMakeRating() {
		Player player = Player.getInstance("TESTPLAYER");
		RatingSystemType[] types = RatingSystemType.values();
		
		for (RatingSystemType type : types) {
			Rating rating = RatingFactory.makeRating(type, player);
			assertNotNull(rating);
		}
	}

}
