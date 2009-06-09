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

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.DuplicateInstanceException;

public class CreateTournament {
	private String tournamentID = "";
	private String userName = "";
	private List<String> errors = new LinkedList<String>();
	
	private boolean correctlyCreated = false;
	
	public boolean isValid() throws SQLException {
		errors.clear();
		
		if (tournamentID.equals("")) {
			errors.add("tournament ID must not be empty");
		}
		if (tournamentID.length() > 40) {
			errors.add("tournament ID must not be longer than 40 characters");
		}
		if (!tournamentID.matches("[a-zA-Z0-9._-]*")) {
			errors.add("tournament ID must only contain the following characters: a-z A-Z 0-9 . _ -");
		} else if (DBConnectorFactory.getDBConnector().getTournament(tournamentID) != null) {
			// this is an "else if" such that only valid user names are checked to prevent SQL injection
			errors.add("tournament ID already exists, please pick a different one");
		}
		
		if (errors.size() > 0) {
			tournamentID = "";
			return false;
		}
		
		return true;
	}
	
	public void createTournament() throws SQLException {
		try {
			DBConnectorFactory.getDBConnector().createTournament(tournamentID, DBConnectorFactory.getDBConnector().getUser(userName));
			correctlyCreated = true;
		} catch (DuplicateInstanceException e) {
			errors.add("tournament ID already exists, please pick a different one");
			correctlyCreated = false;
		}
	}
	
	public boolean isCorrectlyCreated() {
		return correctlyCreated;
	}

	public List<String> getErrors() {
		return errors;
	}

	public String getTournamentID() {
		return tournamentID;
	}

	public void setTournamentID(String tournamentID) {
		this.tournamentID = tournamentID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
