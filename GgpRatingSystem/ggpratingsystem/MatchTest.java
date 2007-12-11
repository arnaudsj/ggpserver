package ggpratingsystem;

import java.io.File;
import java.util.List;


import junit.framework.TestCase;

public class MatchTest extends TestCase {
	private Match match33941143263;
	
	// This is an interesting test case because the XML file contains a reference to the 
	// following external DTD:
	// <!DOCTYPE match SYSTEM "http://games.stanford.edu/gamemaster/xml/viewmatch.dtd">
	private Match match3390055120;	 

	protected void setUp() throws Exception {
		super.setUp();
		String userdir = System.getProperty("user.dir");
		if (!userdir.endsWith(File.separator))
			userdir += File.separator;

		String matchFileName = userdir + "data" + File.separator
				+ "2007_final_round" + File.separator
				+ "Match.33941143263.xml";

		match33941143263 = new Match(new MatchSet("Breakthroughholes_2007_R5_D1", 2007, 5,
				1, 1, Game.getInstance("Breakthroughholes")), 
				new File(matchFileName));		

		matchFileName = userdir + "data" + File.separator
				+ "2007_preliminaries" + File.separator 
				+ "Match.3390055120.xml";

		match3390055120 = new Match(new MatchSet(
				"Blocksworld-Obfuscated_2007_R1_D1", 2007, 1, 1, 1, 
				Game.getInstance("Blocksworld-Obfuscated")), 
				new File(matchFileName));		
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
		assertEquals(match3390055120.getMatchId(), "Match.3390055120");
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
		
		
		players = match3390055120.getPlayers();
		
		assertEquals(players.size(), 1);
		assertEquals(players.get(0), Player.getInstance("THE-PIRATE"));
	}

	/*
	 * Test method for 'ggpratingsystem.Match.getRoles()'
	 */
	public void testGetRoles() {
		List<String> roles = match33941143263.getMatchSet().getGame().getRoles();
		assertEquals(roles.size(), 2);
		assertEquals(roles.get(0), "White");
		assertEquals(roles.get(1), "Black");

		roles = match3390055120.getMatchSet().getGame().getRoles();
		assertEquals(roles.size(), 1);
		assertEquals(roles.get(0), "Mannouremnow");
	}

	/*
	 * Test method for 'ggpratingsystem.Match.getScores()'
	 */
	public void testGetScores() {
		List<Integer> scores = match33941143263.getScores();
		assertEquals(scores.size(), 2);
		assertEquals(scores.get(0).intValue(), 0);
		assertEquals(scores.get(1).intValue(), 100);
		
		scores = match3390055120.getScores();
		assertEquals(scores.size(), 1);
		assertEquals(scores.get(0).intValue(), 0);
	}
}
