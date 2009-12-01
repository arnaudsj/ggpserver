/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 
                  2009 Stephan Schiffel <stephan.schiffel@gmx.de> 

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
import java.util.Collection;

import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Tournament;
import tud.ggpserver.datamodel.User;


public class ViewUser {
	private User user = null;

	public User getUser() {
		return user;
	}

	public void setUserName(String userName) throws SQLException {
		this.user = DBConnectorFactory.getDBConnector().getUser(userName);
	}
	
	public Collection<? extends PlayerInfo> getPlayers() throws SQLException {
		return DBConnectorFactory.getDBConnector().getPlayerInfosForUser(user.getUserName());
	}
	
	public Collection<? extends Tournament<?,?>> getTournaments() throws SQLException {
		return DBConnectorFactory.getDBConnector().getTournamentsCreatedByUser(user.getUserName());
	}
}
