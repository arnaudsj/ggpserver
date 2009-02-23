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
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class MatchSetTest extends TestCase {
	private final List<MatchSet> matchSets;
	private final MatchSet blocksworld;
	private final MatchSet tictactoe;
	private final MatchSet chinesecheckers4p;
	
	
	public MatchSetTest() throws IOException {
		super();
		MatchReader matchReader = new FileMatchReader(new File(Util.getDataDir(), "competition2007" + File.separator + "xml"));

		matchSets = new LinkedList<MatchSet>();
		for (int i = 0; i < 9; i++) {	// intentionally not reading all MatchSets here, are not needed
			assert(matchReader.hasNext());
			matchSets.add(matchReader.readMatchSet());
		}

		blocksworld = matchSets.get(0);	
		tictactoe = matchSets.get(5);
		chinesecheckers4p = matchSets.get(8); 
	}

	public void testGetDay() {
		assertEquals(1, blocksworld.getDay());
		assertEquals(1, tictactoe.getDay());
		assertEquals(1, chinesecheckers4p.getDay());
	}

	public void testGetGame() {
		assertEquals("Blocksworld-Obfuscated", blocksworld.getGame().getName());
		assertEquals("Tictactoe-Obfuscated", tictactoe.getGame().getName());
		assertEquals("Chinesecheckers4p-Obfuscated", chinesecheckers4p.getGame().getName());
	}

	public void testGetId() {
		assertEquals("Blocksworld-Obfuscated_2007_R1_D1", blocksworld.getId());
		assertEquals("Tictactoe-Obfuscated_2007_R1_D1", tictactoe.getId());
		assertEquals("Chinesecheckers4p-Obfuscated_2007_R1_D1", chinesecheckers4p.getId());
	}

	public void testGetRound() {
		assertEquals(1, blocksworld.getRound());
		assertEquals(1, tictactoe.getRound());
		assertEquals(1, chinesecheckers4p.getRound());
	}

	public void testGetYear() {
		assertEquals(2007, blocksworld.getYear());
		assertEquals(2007, tictactoe.getYear());
		assertEquals(2007, chinesecheckers4p.getYear());
	}

	public void testGetMatchSetNumber() {
		assertEquals(1, blocksworld.getMatchSetNumber());
		assertEquals(6, tictactoe.getMatchSetNumber());
		assertEquals(9, chinesecheckers4p.getMatchSetNumber());
	}

	public void testGetMatches() {
		final String[] blocksworldMatchNames = {"Match.3390055120", "Match.3390055121", "Match.3390055122", "Match.3390055194", "Match.3390055196", "Match.3390055197", "Match.3390055198", "Match.3390055482", "Match.3390055897"}; 
		final String[] tictactoeMatchNames = {"Match.3390069730", "Match.3390069731", "Match.3390069732", "Match.3390069733", "Match.339007092", "Match.3390070950", "Match.3390070951", "Match.3390071098"};
		final String[] chinesecheckers4pMatchNames = {"Match.3390083667", "Match.3390083853"};
		
		
		assertArraysEqual(blocksworldMatchNames, listMatchNames(blocksworld));
		assertArraysEqual(tictactoeMatchNames, listMatchNames(tictactoe));
		assertArraysEqual(chinesecheckers4pMatchNames, listMatchNames(chinesecheckers4p));
	}

	public void testOverallScores() {
		Map<Player, Double> overallScores = blocksworld.overallScores();
		assertEquals(9, overallScores.size());
		assertEquals(100.0, overallScores.get(Player.getInstance("FLUXPLAYER")));
		assertEquals(100.0, overallScores.get(Player.getInstance("U-TEXAS-LARG")));
		assertEquals(100.0, overallScores.get(Player.getInstance("CLUNEPLAYER")));
		assertEquals(100.0, overallScores.get(Player.getInstance("JIGSAWBOT")));
		assertEquals(100.0, overallScores.get(Player.getInstance("W-WOLFE")));
		assertEquals(100.0, overallScores.get(Player.getInstance("ARY")));
		assertEquals(100.0, overallScores.get(Player.getInstance("CADIA-PLAYER")));
		assertEquals(100.0, overallScores.get(Player.getInstance("LUCKY-LEMMING")));
		assertEquals(  0.0, overallScores.get(Player.getInstance("THE-PIRATE")));
		
		overallScores = tictactoe.overallScores();
		assertEquals(8, overallScores.size());
		assertEquals(100.0, overallScores.get(Player.getInstance("FLUXPLAYER")));
		assertEquals(200.0, overallScores.get(Player.getInstance("U-TEXAS-LARG")));
		assertEquals(100.0, overallScores.get(Player.getInstance("CLUNEPLAYER")));
		assertEquals(  0.0, overallScores.get(Player.getInstance("JIGSAWBOT")));
		assertEquals(  0.0, overallScores.get(Player.getInstance("W-WOLFE")));
		assertEquals(100.0, overallScores.get(Player.getInstance("ARY")));
		assertEquals(200.0, overallScores.get(Player.getInstance("CADIA-PLAYER")));
		assertEquals(100.0, overallScores.get(Player.getInstance("LUCKY-LEMMING")));
		assertEquals( null, overallScores.get(Player.getInstance("THE-PIRATE")));
		
		overallScores = chinesecheckers4p.overallScores();
		assertEquals(8, overallScores.size());
		assertEquals(100.0, overallScores.get(Player.getInstance("FLUXPLAYER")));
		assertEquals( 50.0, overallScores.get(Player.getInstance("U-TEXAS-LARG")));
		assertEquals( 75.0, overallScores.get(Player.getInstance("CLUNEPLAYER")));
		assertEquals(100.0, overallScores.get(Player.getInstance("JIGSAWBOT")));
		assertEquals( null, overallScores.get(Player.getInstance("W-WOLFE")));
		assertEquals( 75.0, overallScores.get(Player.getInstance("ARY")));
		assertEquals( null, overallScores.get(Player.getInstance("CADIA-PLAYER")));
		assertEquals( null, overallScores.get(Player.getInstance("LUCKY-LEMMING")));
		assertEquals( null, overallScores.get(Player.getInstance("THE-PIRATE")));
		assertEquals( 25.0, overallScores.get(Player.getInstance("RANDOM")));
		assertEquals( 25.0, overallScores.get(Player.getInstance("RANDOM2")));
		assertEquals( 50.0, overallScores.get(Player.getInstance("RANDOM3")));
	}
	
//	public void testCountRoles() {
//		for (MatchSet matchSet : matchSets) {
//			System.out.println(matchSet);
//			Set<Player> players = matchSet.getPlayers();
//			for (Player player : players) {
//				System.out.println(matchSet.countRoles().get(player));
//			}
//			System.out.println();
//		}
//	}


	private void assertArraysEqual(Object[] expected, Object[] actual) {
		assertEquals(expected.length, actual.length);
		
		for (int i = 0; i < expected.length; i++) {
			assertEquals(expected[i], actual[i]);
		}
	}

	private String[] listMatchNames(MatchSet matchSet) {
		List<Match> matches = matchSet.getMatches();
		String[] result = new String[matches.size()];
		
		for (int i = 0; i < result.length; i++) {
			result[i] = matches.get(i).getMatchId();
		}
		return result;
	}
}
