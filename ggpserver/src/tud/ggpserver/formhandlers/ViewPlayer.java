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
import java.util.List;

import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.players.LocalPlayerInfo;
import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.RemoteOrHumanPlayerInfo;
import tud.ggpserver.datamodel.RemotePlayerInfo;
import tud.ggpserver.datamodel.Tournament;
import tud.ggpserver.datamodel.User;

public class ViewPlayer {
	private PlayerInfo playerInfo = null;
	private List<? extends Tournament<?,?>> tournaments = null;
	
	public void setName(String name) throws SQLException {
		playerInfo = DBConnectorFactory.getDBConnector().getPlayerInfo(name);
	}
	
	public boolean isUser () throws SQLException {
		return DBConnectorFactory.getDBConnector().isUser(playerInfo.getName());
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
			return ((RemotePlayerInfo) playerInfo).getStatus().toString();
		}else if(playerInfo instanceof LocalPlayerInfo){
			return RemoteOrHumanPlayerInfo.STATUS_ACTIVE;
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
		if(tournaments == null)
			tournaments = DBConnectorFactory.getDBConnector().getTournamentsForPlayer(playerInfo.getName());
		return tournaments;
	}

	public int getNumberOfTournaments() throws SQLException {
		return getTournaments().size();
	}

	public GDLVersion getGdlVersion () {
		return playerInfo.getGdlVersion();
	}
	
}
