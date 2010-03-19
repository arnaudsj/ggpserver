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

import tud.gamecontroller.game.impl.Game;
import tud.ggpserver.datamodel.DuplicateInstanceException;
import tud.ggpserver.util.Utilities;

public class CreateGame extends AbstractGameValidator {
	private boolean correctlyCreated = false;
	
	public void create() throws SQLException {
		assert(isValid());
		try {
			String sendSeesXMLRules = getSeesXMLRules().trim();
			if(sendSeesXMLRules.isEmpty() || sendSeesXMLRules.equals(Game.DEFAULT_SEES_XML_RULES.trim()))
				sendSeesXMLRules=null;
			getDBConnector().createGame(
					getGameDescription(), getGameName(), getStylesheet(),
					sendSeesXMLRules, getEnabled(), getCreator(), Utilities.gdlVersion(getGdlVersion()));
			correctlyCreated = true;
		} catch (DuplicateInstanceException e) {
			getErrorsGameName().add("game name already exists, please pick a different one");
			correctlyCreated = false;
		}
	}

	public boolean isCorrectlyCreated() {
		return correctlyCreated;
	}

	@Override
	protected boolean isGameExpectedToExist() {
		return false;
	}
}
