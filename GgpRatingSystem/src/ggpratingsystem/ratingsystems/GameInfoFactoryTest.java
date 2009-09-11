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

import static ggpratingsystem.ratingsystems.RatingSystemType.DIRECT_SCORES;
import ggpratingsystem.Game;
import ggpratingsystem.GameSet;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public class GameInfoFactoryTest extends TestCase {

	public void testMakeGameInfo() {
		List<String> roles = new LinkedList<String>();
		roles.add("role1");
		roles.add("role2");
		
		GameSet gameSet = new GameSet();
		Game game = gameSet.getGame("GameInfoFactoryTest_TESTGAME");
		game.setRoles(roles);
		
		RatingSystemType[] types = RatingSystemType.values();
		
		for (RatingSystemType type : types) {
			if (type.equals(DIRECT_SCORES)) {
				continue;	// direct scores does not need a game info
			}
			AbstractGameInfo gameInfo = GameInfoFactory.makeGameInfo(type, game);
			assertNotNull(gameInfo);
		}
	}

}
