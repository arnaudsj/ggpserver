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
import java.util.List;
import java.util.logging.Logger;

import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.RemotePlayerInfo;

public class ShowPlayers extends AbstractPager {
	private static final Logger logger = Logger.getLogger(ShowPlayers.class.getName());

	protected final static AbstractDBConnector<?, ?> db = DBConnectorFactory.getDBConnector();

	public List<PlayerInfo> getPlayers() throws SQLException {
		List<PlayerInfo> result = db.getPlayerInfos(startRow, numDisplayedRows);
		
		if (!onlyRemotePlayerInfos(result)) {
			logger.severe("DBConnector.getPlayerInfos() returned a PlayerInfo that was not a RemotePlayerInfo!"); //$NON-NLS-1$
			// this must not happen because show_players.jsp will call getOwner() on
			// the result, which is only defined for RemotePlayerInfos.
			assert(false);  
		}
		
		return result;
	}
	
	private boolean onlyRemotePlayerInfos(List<PlayerInfo> result) {
		for (PlayerInfo info : result) {
			if (!(info instanceof RemotePlayerInfo)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String getTableName() {
		return "players";
	}

	@Override
	public String getTargetJsp() {
		return "show_players.jsp";
	}
}
