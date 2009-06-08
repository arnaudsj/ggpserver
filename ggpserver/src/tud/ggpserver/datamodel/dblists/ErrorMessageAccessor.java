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

package tud.ggpserver.datamodel.dblists;

import java.sql.SQLException;
import java.util.List;

import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.ggpserver.datamodel.AbstractDBConnector;

public class ErrorMessageAccessor implements DBAccessor<List<GameControllerErrorMessage>>{
	private final AbstractDBConnector<?, ?> db;
	private final String matchID;

	public ErrorMessageAccessor(final String matchID, final AbstractDBConnector<?, ?> db) {
		super();
		this.db = db;
		this.matchID = matchID;
	}

	public List<List<GameControllerErrorMessage>> getAllElements() throws SQLException {
		return db.getAllErrorMessages(matchID, getSize());
	}

	public List<GameControllerErrorMessage> getElement(int stepNumber) throws SQLException {
		return db.getErrorMessages(matchID, stepNumber + 1);  // stepNumber counting in DB starts from 1
	}

	public int getSize() throws SQLException {
		// this is intentionally numberOfXMLStates, because there is always
		// one list of error messages for each state.
		return db.getNumberOfXMLStates(matchID);
	}

}
