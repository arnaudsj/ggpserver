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

import tud.ggpserver.datamodel.AbstractDBConnector;

public class JointMovesAccessor implements DBAccessor<List<String>>{
	private final AbstractDBConnector<?, ?> db;
	private final String matchID;

	public JointMovesAccessor(final String matchID, final AbstractDBConnector<?, ?> db) {
		super();
		this.db = db;
		this.matchID = matchID;
	}

	public List<List<String>> getAllElements() throws SQLException {
		return db.getJointMovesStrings(matchID);
	}

	public List<String> getElement(int stepNumber) throws SQLException {
		return db.getJointMove(matchID, stepNumber + 1);   // stepNumber starts from 0, in DB from 1
	}

	public int getSize() throws SQLException {
		return db.getNumberOfXMLStates(matchID) - 1;
	}

}
