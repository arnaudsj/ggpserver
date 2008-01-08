package ggpratingsystem;

import static ggpratingsystem.RatingSystemType.LINEAR_REGRESSION;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import junit.framework.TestCase;

public class LinearRegressionGameInfoTest extends TestCase {
	private LinearRegressionGameInfo testGameInfo;
	private Game testgame;
	
	private final List<MatchSet> matchSets;
//	private MatchSet blocksworld;
//	private MatchSet tictactoe;
//	private MatchSet chinesecheckers4p;
	
	public LinearRegressionGameInfoTest() throws IOException {
		super();
		matchSets = MatchReader.readDataDir("2007_preliminaries");
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();

//		blocksworld = matchSets.get(0);	
//		tictactoe = matchSets.get(5);
//		chinesecheckers4p = matchSets.get(8);
		
		LinkedList<String> roles = new LinkedList<String>();
		roles.add("Testrole1");
		roles.add("Testrole2");
		
		testgame = Game.getInstance("TESTGAME");
		testgame.setRoles(roles);
		testGameInfo = new LinearRegressionGameInfo(testgame);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		for (MatchSet matchSet : matchSets) {
			matchSet.getGame().getGameInfo(LINEAR_REGRESSION).reset();		
		}
		
		Player.getInstance("FLUXPLAYER").getRating(LINEAR_REGRESSION).reset();
		Player.getInstance("U-TEXAS-LARG").getRating(LINEAR_REGRESSION).reset();
		Player.getInstance("CLUNEPLAYER").getRating(LINEAR_REGRESSION).reset();
		Player.getInstance("JIGSAWBOT").getRating(LINEAR_REGRESSION).reset();
		Player.getInstance("W-WOLFE").getRating(LINEAR_REGRESSION).reset();
		Player.getInstance("ARY").getRating(LINEAR_REGRESSION).reset();
		Player.getInstance("CADIA-PLAYER").getRating(LINEAR_REGRESSION).reset();
		Player.getInstance("LUCKY-LEMMING").getRating(LINEAR_REGRESSION).reset();
		Player.getInstance("THE-PIRATE").getRating(LINEAR_REGRESSION).reset();
		Player.getInstance("RANDOM").getRating(LINEAR_REGRESSION).reset();
		Player.getInstance("RANDOM2").getRating(LINEAR_REGRESSION).reset();
		Player.getInstance("RANDOM3").getRating(LINEAR_REGRESSION).reset();
	}
		
	public void testGetType() {
		assertEquals(LINEAR_REGRESSION, testGameInfo.getType());
	}

	public void testUpdateGameInfoThrowsException() {
		// feed a MatchSet of game "blocksworld" to GameInfo of game "testgame"
		try {
			testGameInfo.updateGameInfo(matchSets.get(0));
		} catch (IllegalArgumentException e) {
			return;		// should throw exception
		}		
		fail("Wrong game - should throw Exception!");
	}

	public void testExpectedScoresThrowsException() {
		// feed a MatchSet of game "blocksworld" to GameInfo of game "testgame"
		try {
			testGameInfo.expectedScores(matchSets.get(0));
		} catch (IllegalArgumentException e) {
			return;		// should throw exception
		}		
		fail("Wrong game - should throw Exception!");
	}

	public void testGetGame() {
		assertEquals(Game.getInstance("TESTGAME"), testGameInfo.getGame());
	}
	
	/*  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  
	 * The following tests are testing the two methods expectedScores() and updateGameInfo().
	 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  */

	/**
	 * expectedScores without any updateGameInfo beforehand should give
	 * LinearRegressionGameInfo.DEFAULT_EXPECTED_SCORE to all players
	 */
	public void testExpectedScoresUntrained() {
		for (MatchSet matchSet : matchSets) {
			testExpectedScoresUntrained(matchSet);
		}
	}
	
	public void testExpectedScoresUntrained(MatchSet matchSet) {
		LinearRegressionGameInfo gameInfo = (LinearRegressionGameInfo) matchSet
				.getGame().getGameInfo(LINEAR_REGRESSION);

		Map<Player, Double> expectedScores = gameInfo.expectedScores(matchSet);
		Map<Player, Integer> numMatchesPerPlayer = matchSet.numMatchesPerPlayer();
		
		Set<Player> players = matchSet.getPlayers();
		
		assert(players.equals(expectedScores.keySet()));
		
		for (Player player : players) {
			double correctExpectedScore = LinearRegressionGameInfo.DEFAULT_EXPECTED_SCORE * numMatchesPerPlayer.get(player);
			assertEquals(correctExpectedScore, expectedScores.get(player), 0.001);
		}
	}
	
	/**
	 * expectedScores with initial player ratings should give mean of all player
	 * scores to all players
	 */
	public void testExpectedScoresTrainedOnce() {
		for (MatchSet matchSet : matchSets) {
			testExpectedScoresTrainedOnce(matchSet);
		}
	}
	
	public void testExpectedScoresTrainedOnce(MatchSet matchSet) {
		LinearRegressionGameInfo gameInfo = (LinearRegressionGameInfo) matchSet
				.getGame().getGameInfo(LINEAR_REGRESSION);

		gameInfo.updateGameInfo(matchSet);
		
		/*
		 * since all ratings were equal and did therefore not provide any
		 * information whatsoever to the linear regression, each player's rating
		 * should be the expected score without ratings
		 */
		Map<Player, Double> expectedScores = gameInfo.expectedScores(matchSet);
		Set<Entry<Player, Double>> entrySet = expectedScores.entrySet();

		for (Entry<Player, Double> entry : entrySet) {
			double expectedScoreWithoutRatings = matchSet.expectedScoreWithoutRatings().get(entry.getKey());

			assertEquals("matchSet: " + matchSet, expectedScoreWithoutRatings, entry.getValue(), 1.0);
		}
	}

	/**
	 * expectedScores twice in a row, without updateGameInfo in between, should give same result twice
	 */
	public void testExpectedScoresTwiceInARow() {
		for (MatchSet matchSet : matchSets) {
			testExpectedScoresTwiceInARow(matchSet);
		}
	}

	public void testExpectedScoresTwiceInARow(MatchSet matchSet) {
		LinearRegressionGameInfo gameInfo = (LinearRegressionGameInfo) matchSet
				.getGame().getGameInfo(LINEAR_REGRESSION);

		gameInfo.updateGameInfo(matchSet);

		Map<Player, Double> expectedScores1 = gameInfo.expectedScores(matchSet);
		Map<Player, Double> expectedScores2 = gameInfo.expectedScores(matchSet);
		
		assertEquals(expectedScores1, expectedScores2);
	}

	/**
	 * expectedScores and updateGameInfo with some forged player Ratings
	 */
	public void testExpectedScoresDifferentRankings() {
		for (MatchSet matchSet : matchSets) {
			testExpectedScoresDifferentRankings(matchSet);
		}
	}

	public void testExpectedScoresDifferentRankings(MatchSet matchSet) {
		LinearRegressionGameInfo gameInfo = (LinearRegressionGameInfo) matchSet
				.getGame().getGameInfo(LINEAR_REGRESSION);

		Player.getInstance("CADIA-PLAYER").getRating(LINEAR_REGRESSION).setCurRating(1400.0);
		Player.getInstance("FLUXPLAYER").getRating(LINEAR_REGRESSION).setCurRating(1400.0);
		Player.getInstance("CLUNEPLAYER").getRating(LINEAR_REGRESSION).setCurRating(1350.0);
		Player.getInstance("U-TEXAS-LARG").getRating(LINEAR_REGRESSION).setCurRating(1300.0);
		Player.getInstance("ARY").getRating(LINEAR_REGRESSION).setCurRating(900.0);
		Player.getInstance("JIGSAWBOT").getRating(LINEAR_REGRESSION).setCurRating(700.0);
		Player.getInstance("LUCKY-LEMMING").getRating(LINEAR_REGRESSION).setCurRating(600.0);
		Player.getInstance("W-WOLFE").getRating(LINEAR_REGRESSION).setCurRating(400.0);
		Player.getInstance("THE-PIRATE").getRating(LINEAR_REGRESSION).setCurRating(300.0);
		Player.getInstance("RANDOM").getRating(LINEAR_REGRESSION).setCurRating(200.0);
		Player.getInstance("RANDOM2").getRating(LINEAR_REGRESSION).setCurRating(200.0);
		Player.getInstance("RANDOM3").getRating(LINEAR_REGRESSION).setCurRating(200.0);
		
		gameInfo.updateGameInfo(matchSet);

		Map<Player, Double> expectedScores = gameInfo.expectedScores(matchSet);
		
		System.out.println(expectedScores);
		// TODO: assertions; a little hard to really say anything sensible here
	}
}
