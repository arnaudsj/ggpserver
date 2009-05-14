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

import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Game;

public class GamePicker<TermType extends TermInterface, ReasonerStateInfoType> {
	private Game<TermType, ReasonerStateInfoType> currentGame;
	private final AbstractDBConnector dbConnector;

	public GamePicker(final AbstractDBConnector dbConnector) {
		this.dbConnector = dbConnector;
		try {
			initCurrentGame();
		} catch (SQLException e) {
			throw new InternalError("Could not get games from database! " + e);
		}
	}

	@SuppressWarnings("unchecked")
	private void initCurrentGame() throws SQLException {
		assert(currentGame == null);  // because this is only called in the constructor
		try {
			currentGame = ((AbstractDBConnector) DBConnectorFactory.getDBConnector()).getLastPlayedGame();
			pickNextGame();
		} catch (SQLException e) {
			// ignore. currentGame will remain null.
		}
		if (currentGame == null) {
			// key next_game didn't exist in Config, or game doesn't exist (any more?) in DB.
			// fallback: pick first enabled game.
			List<Game<TermType, ReasonerStateInfoType>> allGames = getDBConnector().getAllEnabledGames();
			if (allGames.isEmpty()) {
				throw new InternalError("No enabled games in Database!");   // shouldn't be an internal error
			}
			currentGame = allGames.get(0);
		}
		assert(currentGame != null);
	}
	
	@SuppressWarnings("unchecked")
	public Game<TermType, ReasonerStateInfoType> pickNextGame() throws SQLException {
		Game<TermType, ReasonerStateInfoType> result = currentGame;
		
		List<Game<TermType, ReasonerStateInfoType>> allGames = getDBConnector().getAllEnabledGames();
		
		int nextGameIndex = (allGames.indexOf(currentGame) + 1) % allGames.size();
		currentGame = allGames.get(nextGameIndex);
		
		DBConnectorFactory.getDBConnector().setLastPlayedGame(currentGame);
		return result;
	}

	private AbstractDBConnector getDBConnector() {
		return dbConnector;
	}
}
