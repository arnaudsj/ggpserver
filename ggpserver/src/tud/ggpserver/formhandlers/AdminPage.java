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
import java.util.List;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.Game;
import tud.ggpserver.scheduler.RoundRobinScheduler;

public class AdminPage {
	private boolean cacheCleared = false;
	
	public boolean isRunning() {
		return RoundRobinScheduler.getInstance().isRunning();
	}
	
	public void setAction(String action) {
		if (action.equals("start")) {
			RoundRobinScheduler.getInstance().start();
		} else if (action.equals("stop")) {
			RoundRobinScheduler.getInstance().stop();
		}
	}

	public boolean isCacheCleared() {
		return cacheCleared;
	}

	public void setCacheCleared(boolean cacheCleared) {
		this.cacheCleared = cacheCleared;
	}
	
	public String getNextPlayedGameName() throws SQLException {
		return RoundRobinScheduler.getInstance().getNextPlayedGame().getName();
	}

	@SuppressWarnings("unchecked")
	public void setNextPlayedGameName(String nextPlayedGame) throws SQLException {
		Game<?, ?> game = getDBConnector().getGame(nextPlayedGame);
		RoundRobinScheduler.getInstance().setNextPlayedGame(game);
	}
	
	@SuppressWarnings("unchecked")
	public List<Game<?, ?>> getAllGames() throws SQLException {
		return ((AbstractDBConnector) getDBConnector()).getAllEnabledGames();
	}
}
