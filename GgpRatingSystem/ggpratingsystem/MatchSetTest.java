package ggpratingsystem;

import java.io.IOException;
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
		matchSets = MatchReader.readSubdir("2007_preliminaries");
		blocksworld = matchSets.get(0);	
		tictactoe = matchSets.get(5);
		chinesecheckers4p = matchSets.get(8); 
	}

	public void testGetDay() {
		assertEquals(blocksworld.getDay(), 1);
		assertEquals(tictactoe.getDay(), 1);
		assertEquals(chinesecheckers4p.getDay(), 1);
	}

	public void testGetGame() {
		assertEquals(blocksworld.getGame().getName(), "Blocksworld-Obfuscated");
		assertEquals(tictactoe.getGame().getName(), "Tictactoe-Obfuscated");
		assertEquals(chinesecheckers4p.getGame().getName(), "Chinesecheckers4p-Obfuscated");
	}

	public void testGetId() {
		assertEquals(blocksworld.getId(), "Blocksworld-Obfuscated_2007_R1_D1");
		assertEquals(tictactoe.getId(), "Tictactoe-Obfuscated_2007_R1_D1");
		assertEquals(chinesecheckers4p.getId(), "Chinesecheckers4p-Obfuscated_2007_R1_D1");
	}

	public void testGetRound() {
		assertEquals(blocksworld.getRound(), 1);
		assertEquals(tictactoe.getRound(), 1);
		assertEquals(chinesecheckers4p.getRound(), 1);
	}

	public void testGetYear() {
		assertEquals(blocksworld.getYear(), 2007);
		assertEquals(tictactoe.getYear(), 2007);
		assertEquals(chinesecheckers4p.getYear(), 2007);
	}

	public void testGetMatchSetNumber() {
		assertEquals(blocksworld.getMatchSetNumber(), 1);
		assertEquals(tictactoe.getMatchSetNumber(), 6);
		assertEquals(chinesecheckers4p.getMatchSetNumber(), 9);
	}

	public void testGetMatches() {
		final String[] blocksworldMatchNames = {"Match.3390055120", "Match.3390055121", "Match.3390055122", "Match.3390055194", "Match.3390055196", "Match.3390055197", "Match.3390055198", "Match.3390055482", "Match.3390055897"}; 
		final String[] tictactoeMatchNames = {"Match.3390069730", "Match.3390069731", "Match.3390069732", "Match.3390069733", "Match.339007092", "Match.3390070950", "Match.3390070951", "Match.3390071098"};
		final String[] chinesecheckers4pMatchNames = {"Match.3390083667", "Match.3390083853"};
		
		
		assertArraysEqual(listMatchNames(blocksworld), blocksworldMatchNames);
		assertArraysEqual(listMatchNames(tictactoe), tictactoeMatchNames);
		assertArraysEqual(listMatchNames(chinesecheckers4p), chinesecheckers4pMatchNames);
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
