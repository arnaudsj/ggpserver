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

import tud.gamecontroller.players.LocalPlayerInfo;
import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.RemotePlayerInfo;
import tud.ggpserver.datamodel.Tournament;
import tud.ggpserver.datamodel.User;

public class ViewPlayer {
	private PlayerInfo playerInfo = null;
	
	public void setName(String name) throws SQLException {
		playerInfo = DBConnectorFactory.getDBConnector().getPlayerInfo(name);
	}
	
	public PlayerInfo getPlayer() {
		return playerInfo;
	}
	
	public User getOwner() throws SQLException {
		if(playerInfo instanceof RemotePlayerInfo){
			return ((RemotePlayerInfo) playerInfo).getOwner();
		}else if(playerInfo instanceof LocalPlayerInfo){
			return DBConnectorFactory.getDBConnector().getAdminUser();
		}
		return null;
	}
	
	public String getStatus() {
		if(playerInfo instanceof RemotePlayerInfo){
			return ((RemotePlayerInfo) playerInfo).getStatus();
		}else if(playerInfo instanceof LocalPlayerInfo){
			return "active";
		}
		return "?";
	}

	public boolean isAvailableForRoundRobinMatches() {
		if(playerInfo instanceof RemotePlayerInfo){
			return ((RemotePlayerInfo) playerInfo).isAvailableForRoundRobinMatches();
		}else if(playerInfo instanceof LocalPlayerInfo){
			return true;
		}
		return false;
	}

	public boolean isAvailableForManualMatches() {
		if(playerInfo instanceof RemotePlayerInfo){
			return ((RemotePlayerInfo) playerInfo).isAvailableForManualMatches();
		}else if(playerInfo instanceof LocalPlayerInfo){
			return true;
		}
		return false;
	}

	public String getName() {
		return playerInfo.getName();
	}
	
	public Collection<? extends Tournament<?,?>> getTournaments() throws SQLException {
		return DBConnectorFactory.getDBConnector().getTournamentsForPlayer(playerInfo.getName());
	}
}
