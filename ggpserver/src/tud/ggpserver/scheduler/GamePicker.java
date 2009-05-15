/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 

    This file is part of GGP Server.

    GGP Server is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GGP Server is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GGP Server.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.ggpserver.scheduler;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.Game;

public class GamePicker<TermType extends TermInterface, ReasonerStateInfoType> {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(GamePicker.class.getName());

	private final AbstractDBConnector<TermType, ReasonerStateInfoType> dbConnector;

	public GamePicker(final AbstractDBConnector<TermType, ReasonerStateInfoType> dbConnector) {
		this.dbConnector = dbConnector;
	}

	/**
	 * Does two things:
	 * - increments the nextPlayedGame 
	 * - returns the old nextPlayedGame
	 * 
	 * Returns null if there are no enabled games in database.
	 */
	public Game<TermType, ReasonerStateInfoType> pickNextGame() throws SQLException {
		Game<TermType, ReasonerStateInfoType> oldNextPlayedGame = getNextPlayedGame();
		
		List<Game<TermType, ReasonerStateInfoType>> allGames = getDBConnector().getAllEnabledGames();

		if (oldNextPlayedGame == null) {
			assert (allGames.isEmpty());   // otherwise, getNextPlayedGame() wouldn't have returned null.
			return null;
		} 

		int newNextPlayedGameIndex = (allGames.indexOf(oldNextPlayedGame) + 1) % allGames.size(); 
		setNextPlayedGame(allGames.get(newNextPlayedGameIndex));
		
		return oldNextPlayedGame;
	}

	////////////////

	public Game<TermType, ReasonerStateInfoType> getNextPlayedGame() throws SQLException {
		Game<TermType, ReasonerStateInfoType> nextPlayedGame = getDBConnector().getNextPlayedGame();

		if (nextPlayedGame == null) {
			// key next_game didn't exist in Config, or game doesn't exist (any more?) in DB.
			// fallback: pick first enabled game.
			List<Game<TermType, ReasonerStateInfoType>> allGames = getDBConnector().getAllEnabledGames();
			if (allGames.isEmpty()) {
				logger.warning("No enabled games in database!"); //$NON-NLS-1$
				return null;
			}
			nextPlayedGame = allGames.get(0);
		}
		
		return nextPlayedGame;
	}

	public void setNextPlayedGame(Game<TermType, ReasonerStateInfoType> nextPlayedGame) throws SQLException {
		getDBConnector().setNextPlayedGame(nextPlayedGame);
	}

	
	private AbstractDBConnector<TermType, ReasonerStateInfoType> getDBConnector() {
		return dbConnector;
	}
}
