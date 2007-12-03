package ggpratingsystem;

import java.io.File;
import java.util.List;


import junit.framework.TestCase;

public class MatchTest extends TestCase {
	private Match match33941143263;

	protected void setUp() throws Exception {
		super.setUp();
		String userdir = System.getProperty("user.dir");
		if (!userdir.endsWith(File.separator))
			userdir += File.separator;

		String matchFileName = userdir + "data" + File.separator
				+ "2007_final_round" + File.separator
				+ "Match.33941143263.xml";

		match33941143263 = new Match(new MatchSet(), new File(matchFileName));		
	}

	/*
	 * Test method for 'ggpratingsystem.Match.Match(MatchSet, Game, File)'
	 */
	public void testMatch() {
		// TODO
	}

	/*
	 * Test method for 'ggpratingsystem.Match.getMatchId()'
	 */
	public void testGetMatchId() {
		assertEquals(match33941143263.getMatchId(), "Match.33941143263");
	}

	/*
	 * Test method for 'ggpratingsystem.Match.getMatchSet()'
	 */
	public void testGetMatchSet() {
		// TODO
	}

	/*
	 * Test method for 'ggpratingsystem.Match.getPlayers()'
	 */
	public void testGetPlayers() {
		List<Player> players = match33941143263.getPlayers();
		
		assertEquals(players.size(), 2);
		assertEquals(players.get(0), Player.getInstance("FLUXPLAYER"));
		assertEquals(players.get(1), Player.getInstance("CLUNEPLAYER"));
	}

	/*
	 * Test method for 'ggpratingsystem.Match.getRoles()'
	 */
	public void testGetRoles() {
		List<String> roles = match33941143263.getRoles();
		assertEquals(roles.size(), 2);
		assertEquals(roles.get(0), "White");
		assertEquals(roles.get(1), "Black");
	}

	/*
	 * Test method for 'ggpratingsystem.Match.getScores()'
	 */
	public void testGetScores() {
		List<Integer> scores = match33941143263.getScores();
		assertEquals(scores.size(), 2);
		assertEquals(scores.get(0).intValue(), 0);
		assertEquals(scores.get(1).intValue(), 100);
	}
}
