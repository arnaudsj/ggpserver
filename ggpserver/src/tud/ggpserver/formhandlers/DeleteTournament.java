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

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Tournament;
import tud.ggpserver.datamodel.User;

public class DeleteTournament {
	private Tournament<?, ?> tournament = null;
	private User user = null;
	private String returnURL = "";
	
	@SuppressWarnings("unchecked")
	private final AbstractDBConnector db = DBConnectorFactory.getDBConnector();
	
	public boolean isValid() throws SQLException {
		if (!tournament.isDeletable())
			return false;
		
		if (tournament.getOwner().equals(user))
			return true;
		
		if (user.isAdmin())
			return true;
		
		return false;
	}
	
	public void delete() throws SQLException {
		db.deleteTournament(tournament.getTournamentID());
	}
	
	public String getReturnURL() {
		return returnURL;
	}
	public void setReturnURL(String returnURL) {
		this.returnURL = returnURL;
	}
	
	public void setTournamentID(String tournamentID) throws SQLException {
		tournament = db.getTournament(tournamentID);
	}
	
	public void setUserName(String userName) throws SQLException {
		user = getDBConnector().getUser(userName);
	}
	
}
