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
import java.util.logging.Logger;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.ConfigOption;
import tud.ggpserver.datamodel.Game;
import tud.ggpserver.datamodel.Tournament;
import tud.ggpserver.scheduler.AbstractRoundRobinScheduler;
import tud.ggpserver.scheduler.RoundRobinScheduler;

public class AdminPage {
	private boolean cacheCleared = false;

	private static final Logger logger = Logger.getLogger(AdminPage.class.getName());

	public boolean isRunning() {
		return RoundRobinScheduler.getInstance().isRunning();
	}

	public boolean isBeingStopped() {
		return RoundRobinScheduler.getInstance().isBeingStopped();
	}

	public void setAction(String action) {
		if (action.equals("start")) {
			RoundRobinScheduler.getInstance().start();
		} else if (action.equals("stop")) {
			RoundRobinScheduler.getInstance().stop();
		} else if (action.equals("stopGracefully")) {
			RoundRobinScheduler.getInstance().stopGracefully();
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

	public void setNextPlayedGameName(String nextPlayedGame) throws SQLException {
		RoundRobinScheduler.getInstance().setNextPlayedGame(nextPlayedGame);
	}
	
	public String getStartclockMin() throws SQLException {
		return getDBConnector().getConfigOption(ConfigOption.START_CLOCK_MIN);
	}

	public String getStartclockMax() throws SQLException {
		return getDBConnector().getConfigOption(ConfigOption.START_CLOCK_MAX);
	}

	public String getPlayclockMin() throws SQLException {
		return getDBConnector().getConfigOption(ConfigOption.PLAY_CLOCK_MIN);
	}

	public String getPlayclockMax() throws SQLException {
		return getDBConnector().getConfigOption(ConfigOption.PLAY_CLOCK_MAX);
	}

	public void setStartclockMin(String s) throws SQLException {
		setClockOption(ConfigOption.START_CLOCK_MIN, s);
	}

	public void setStartclockMax(String s) throws SQLException {
		setClockOption(ConfigOption.START_CLOCK_MAX, s);
	}

	public void setPlayclockMin(String s) throws SQLException {
		setClockOption(ConfigOption.PLAY_CLOCK_MIN, s);
	}

	public void setPlayclockMax(String s) throws SQLException {
		setClockOption(ConfigOption.PLAY_CLOCK_MAX, s);
	}

	private void setClockOption(ConfigOption option, String s) throws SQLException {
		try {
			int i=Integer.parseInt(s);
			if( i > 0 && (i % 5 == 0) ) {
				getDBConnector().setConfigOption(option, Integer.toString(i));
			}else{
				logger.warning("wrong value for "+option+": " + i + " is out of range or not a multiple of 5");
			}
		} catch (NumberFormatException e) {
			logger.warning("NumberFormatException while parsing config option " + option + ":" + e.getMessage());
		}
	}

	public List<? extends Game<?, ?>> getAllGames() throws SQLException {
		return ((AbstractDBConnector<?,?>) getDBConnector()).getAllEnabledGames();
	}
	
	public List<? extends Tournament<?, ?>> getTournaments() throws SQLException {
		List<? extends Tournament<?, ?>> tournaments = getDBConnector().getTournaments();
		tournaments.remove(getDBConnector().getTournament(AbstractRoundRobinScheduler.ROUND_ROBIN_TOURNAMENT_ID));
		return tournaments;
	}
}
