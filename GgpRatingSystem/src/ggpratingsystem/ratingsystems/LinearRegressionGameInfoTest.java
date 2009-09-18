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

import static ggpratingsystem.ratingsystems.RatingSystemType.DYNAMIC_LINEAR_REGRESSION;

import ggpratingsystem.Configuration;
import ggpratingsystem.Game;
import ggpratingsystem.FileMatchSetReader;
import ggpratingsystem.MatchSetReader;
import ggpratingsystem.MatchSet;
import ggpratingsystem.Player;
import ggpratingsystem.util.Util;

import java.io.File;
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
	private Configuration configuration;
	
	private final List<MatchSet> matchSets;
//	private MatchSet blocksworld;
//	private MatchSet tictactoe;
//	private MatchSet chinesecheckers4p;
	
	public LinearRegressionGameInfoTest() throws IOException {
		super();
		configuration = new Configuration();
		MatchSetReader matchSetReader = new FileMatchSetReader(new File(Util.getDataDir(), "competition2007" + File.separator + "xml"), configuration);
		
		matchSets = new LinkedList<MatchSet>();
		while (matchSetReader.hasNext()) {
			matchSets.add(matchSetReader.readMatchSet());
		}
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
		
		testgame = configuration.getGameSet().getGame("TESTGAME");
		testgame.setRoles(roles);
		testGameInfo = (LinearRegressionGameInfo) GameInfoFactory.makeGameInfo(
				DYNAMIC_LINEAR_REGRESSION, testgame);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		
		for (MatchSet matchSet : matchSets) {
			matchSet.getGame().getGameInfo(DYNAMIC_LINEAR_REGRESSION).reset();		
		}
		
		configuration.getPlayerSet().getPlayer("FLUXPLAYER").getRating(DYNAMIC_LINEAR_REGRESSION).reset();
		configuration.getPlayerSet().getPlayer("U-TEXAS-LARG").getRating(DYNAMIC_LINEAR_REGRESSION).reset();
		configuration.getPlayerSet().getPlayer("CLUNEPLAYER").getRating(DYNAMIC_LINEAR_REGRESSION).reset();
		configuration.getPlayerSet().getPlayer("JIGSAWBOT").getRating(DYNAMIC_LINEAR_REGRESSION).reset();
		configuration.getPlayerSet().getPlayer("W-WOLFE").getRating(DYNAMIC_LINEAR_REGRESSION).reset();
		configuration.getPlayerSet().getPlayer("ARY").getRating(DYNAMIC_LINEAR_REGRESSION).reset();
		configuration.getPlayerSet().getPlayer("CADIA-PLAYER").getRating(DYNAMIC_LINEAR_REGRESSION).reset();
		configuration.getPlayerSet().getPlayer("LUCKY-LEMMING").getRating(DYNAMIC_LINEAR_REGRESSION).reset();
		configuration.getPlayerSet().getPlayer("THE-PIRATE").getRating(DYNAMIC_LINEAR_REGRESSION).reset();
		configuration.getPlayerSet().getPlayer("RANDOM").getRating(DYNAMIC_LINEAR_REGRESSION).reset();
		configuration.getPlayerSet().getPlayer("RANDOM2").getRating(DYNAMIC_LINEAR_REGRESSION).reset();
		configuration.getPlayerSet().getPlayer("RANDOM3").getRating(DYNAMIC_LINEAR_REGRESSION).reset();
	}
		
	public void testGetType() {
		assertEquals(DYNAMIC_LINEAR_REGRESSION, testGameInfo.getType());
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
		assertEquals(configuration.getGameSet().getGame("TESTGAME"), testGameInfo.getGame());
	}
	
	/*  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  
	 * The following tests are testing the two methods expectedScores() and updateGameInfo().
	 *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  *  */

	/**
	 * expectedScores without any updateGameInfo beforehand should give
	 * LinearRegressionGameInfo.DEFAULT_EXPECTED_SCORE to all players
	 */
	public void testExpectedScoresUntrained() {
		for (Player player : configuration.getPlayerSet().getAllPlayers()) {
			player.getRating(DYNAMIC_LINEAR_REGRESSION).reset();
		}
		
		for (MatchSet matchSet : matchSets) {
			testExpectedScoresUntrained(matchSet);
		}
	}
	
	public void testExpectedScoresUntrained(MatchSet matchSet) {
		LinearRegressionGameInfo gameInfo = (LinearRegressionGameInfo) matchSet
				.getGame().getGameInfo(DYNAMIC_LINEAR_REGRESSION);

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
		for (Player player : configuration.getPlayerSet().getAllPlayers()) {
			player.getRating(DYNAMIC_LINEAR_REGRESSION).reset();
		}
		
		for (MatchSet matchSet : matchSets) {
			testExpectedScoresTrainedOnce(matchSet);
		}
	}
	
	public void testExpectedScoresTrainedOnce(MatchSet matchSet) {
		LinearRegressionGameInfo gameInfo = (LinearRegressionGameInfo) matchSet
				.getGame().getGameInfo(DYNAMIC_LINEAR_REGRESSION);

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
				.getGame().getGameInfo(DYNAMIC_LINEAR_REGRESSION);

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
				.getGame().getGameInfo(DYNAMIC_LINEAR_REGRESSION);

		configuration.getPlayerSet().getPlayer("CADIA-PLAYER").getRating(DYNAMIC_LINEAR_REGRESSION).setCurRating(1400.0);
		configuration.getPlayerSet().getPlayer("FLUXPLAYER").getRating(DYNAMIC_LINEAR_REGRESSION).setCurRating(1400.0);
		configuration.getPlayerSet().getPlayer("CLUNEPLAYER").getRating(DYNAMIC_LINEAR_REGRESSION).setCurRating(1350.0);
		configuration.getPlayerSet().getPlayer("U-TEXAS-LARG").getRating(DYNAMIC_LINEAR_REGRESSION).setCurRating(1300.0);
		configuration.getPlayerSet().getPlayer("ARY").getRating(DYNAMIC_LINEAR_REGRESSION).setCurRating(900.0);
		configuration.getPlayerSet().getPlayer("JIGSAWBOT").getRating(DYNAMIC_LINEAR_REGRESSION).setCurRating(700.0);
		configuration.getPlayerSet().getPlayer("LUCKY-LEMMING").getRating(DYNAMIC_LINEAR_REGRESSION).setCurRating(600.0);
		configuration.getPlayerSet().getPlayer("W-WOLFE").getRating(DYNAMIC_LINEAR_REGRESSION).setCurRating(400.0);
		configuration.getPlayerSet().getPlayer("THE-PIRATE").getRating(DYNAMIC_LINEAR_REGRESSION).setCurRating(300.0);
		configuration.getPlayerSet().getPlayer("RANDOM").getRating(DYNAMIC_LINEAR_REGRESSION).setCurRating(200.0);
		configuration.getPlayerSet().getPlayer("RANDOM2").getRating(DYNAMIC_LINEAR_REGRESSION).setCurRating(200.0);
		configuration.getPlayerSet().getPlayer("RANDOM3").getRating(DYNAMIC_LINEAR_REGRESSION).setCurRating(200.0);
		
		gameInfo.updateGameInfo(matchSet);

		// call this so that the expected scores are logged
		gameInfo.expectedScores(matchSet);
		
		// TO DO: assertions; a little hard to really say anything sensible here
	}
}
