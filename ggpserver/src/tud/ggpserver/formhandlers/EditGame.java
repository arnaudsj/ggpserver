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

package tud.ggpserver.formhandlers;

import static tud.ggpserver.datamodel.DBConnectorFactory.getDBConnector;

import java.sql.SQLException;

import tud.ggpserver.datamodel.Game;
import tud.ggpserver.util.Utilities;

public class EditGame extends AbstractGameValidator {
	private boolean correctlyUpdated = false;
	
	public void updateGame() throws SQLException {
		assert(isValid());
		String sendSeesXMLRules = getSeesXMLRules();
		if(sendSeesXMLRules.equals(Game.DEFAULT_SEES_XML_RULES))
			sendSeesXMLRules=null;
		getDBConnector().updateGameInfo(getGameName(), getGameDescription(), getStylesheet(), sendSeesXMLRules, getEnabled(), Utilities.gdlVersion(getGdlVersion()));
		correctlyUpdated = true;
	}
	
	public boolean isCorrectlyUpdated() {
		return correctlyUpdated;
	}

	@Override
	public void setGameName(String gameName) {
		super.setGameName(gameName);
		try {
			Game<?, ?> game = getDBConnector().getGame(gameName);
			if (game == null) {
				getErrorsGameName().add("there is no game with name '" + gameName + "'");
			} else {
				setGameDescription(game.getGameDescription());
				setStylesheet(game.getStylesheet());
				setEnabled(game.isEnabled());
				setGdlVersion(Utilities.gdlVersion(game.getGdlVersion()));
				setSeesXMLRules(game.getSeesXMLRules());
			}
		} catch (SQLException e) {
			getErrorsGameName().add("there was an error getting game '" + gameName + "'"
					+ " from the database: " + e.getMessage());
		}
	}

	@Override
	protected boolean isGameExpectedToExist() {
		return true;
	}
}
