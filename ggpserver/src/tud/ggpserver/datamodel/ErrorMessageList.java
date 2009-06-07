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

package tud.ggpserver.datamodel;

import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import tud.gamecontroller.logging.GameControllerErrorMessage;

public class ErrorMessageList extends AbstractList<List<GameControllerErrorMessage>> {
	private final AbstractDBConnector<?, ?> db;
	private final String matchID;
	
	private List<List<GameControllerErrorMessage>> cache = new ArrayList<List<GameControllerErrorMessage>>();
	
	
	public ErrorMessageList(final String matchID, final AbstractDBConnector<?, ?> db) {
		this.db = db;
		this.matchID = matchID;
	}

	@Override
	public List<GameControllerErrorMessage> get(int stepNumber) {
		if (stepNumber < 0 || stepNumber >= size()) {
			throw new IndexOutOfBoundsException();
		}
		try {
			growCache(stepNumber + 1);
			
			List<GameControllerErrorMessage> result = cache.get(stepNumber);
			if (result == null) {
				result = db.getErrorMessages(matchID, stepNumber);
				cache.set(stepNumber, result);
			}
			return result;
		} catch (SQLException e1) {
			IndexOutOfBoundsException e2 = new IndexOutOfBoundsException(e1.getMessage());
			e2.initCause(e1);
			throw e2;
		}
	}


	/**
	 * Grows the cache so it can hold at least <code>size</code> elements.
	 */
	private void growCache(int size) {
		while (cache.size() < size) {
			cache.add(null);
		}
	}
	
	@Override
	public int size() {
		try {
			// this is intentionally numberOfXMLStates, because there is always
			// one list of error messages for each state.
			return db.getNumberOfXMLStates(matchID);
		} catch (SQLException e) {
			InternalError internalError = new InternalError(e.getMessage());
			internalError.initCause(e);
			throw internalError;
		}
	}
}