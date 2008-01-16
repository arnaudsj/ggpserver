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
