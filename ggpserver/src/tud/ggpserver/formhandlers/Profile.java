/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 
                  2009 Stephan Schiffel <stephan.schiffel@gmx.de>
                  2010 Nicolas JEAN <njean42@gmail.com>

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
import java.util.LinkedList;
import java.util.List;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.RemotePlayerInfo;
import tud.ggpserver.datamodel.Tournament;
import tud.ggpserver.datamodel.User;

public class Profile {
	protected final AbstractDBConnector<?, ?> db = DBConnectorFactory.getDBConnector();
	private User user = null;
	
	public String getUserName() {
		if (user == null) {
			throw new IllegalStateException("user not set!");
		}
		return user.getUserName();
	}

	public void setUserName(String userName) throws SQLException {
		user = getDBConnector().getUser(userName);
	}
	
	public List<RemotePlayerInfo> getPlayers() throws SQLException {
		assert (user != null);
		return db.getPlayerInfosForUser(user.getUserName());
	}
	
	public List<? extends Tournament<?,?>> getTournaments() throws SQLException {
		AbstractDBConnector<?, ?> db = getDBConnector();
		List<Tournament<?,?>> tournaments = new LinkedList<Tournament<?,?>>(db.getTournamentsCreatedByUser(getUserName()));
		if(!user.equals(db.getAdminUser()))
			tournaments.add(0, db.getTournament(Tournament.MANUAL_TOURNAMENT_ID));
		return tournaments;
	}
	
}
