package ggpratingsystem.ratingsystems;

import ggpratingsystem.Game;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public class GameInfoFactoryTest extends TestCase {

	public void testMakeGameInfo() {
		List<String> roles = new LinkedList<String>();
		roles.add("role1");
		roles.add("role2");
		
		Game game = Game.getInstance("GameInfoFactoryTest_TESTGAME");
		game.setRoles(roles);
		
		RatingSystemType[] types = RatingSystemType.values();
		
		for (RatingSystemType type : types) {
			AbstractGameInfo gameInfo = GameInfoFactory.makeGameInfo(type, game);
			assertNotNull(gameInfo);
		}
	}

}
