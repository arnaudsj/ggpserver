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

package ggpratingsystem;

import ggpratingsystem.util.Util;

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
		File matchdir = new File(Util.getDataDir(), "competition2007" + File.separator + "xml");

		

		File matchFile = new File(matchdir, "Match.33941143263.xml");

		match33941143263 = new Match(new MatchSet("Breakthroughholes_2007_R5_D1", 2007, 5,
				1, 1, Game.getInstance("Breakthroughholes")), 
				matchFile);		

		matchFile = new File(matchdir, "Match.3390055120.xml");

		match3390055120 = new Match(new MatchSet(
				"Blocksworld-Obfuscated_2007_R1_D1", 2007, 1, 1, 1, 
				Game.getInstance("Blocksworld-Obfuscated")), 
				matchFile);		
	}


	/*
	 * Test method for 'ggpratingsystem.Match.getMatchId()'
	 */
	public void testGetMatchId() {
		assertEquals(match33941143263.getMatchId(), "Match.33941143263");
		assertEquals(match3390055120.getMatchId(), "Match.3390055120");
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
