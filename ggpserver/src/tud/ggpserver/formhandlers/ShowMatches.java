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

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Match;

public class ShowMatches extends AbstractPager {
	private String playerName = null;
	private String gameName = null;
	private String tournamentID = null;

	@SuppressWarnings("unchecked")
	private List<Match> matches = null;

	@SuppressWarnings("unchecked")
	protected final static AbstractDBConnector db = DBConnectorFactory.getDBConnector();
	
	@SuppressWarnings("unchecked")
	public List<Match> getMatches() throws SQLException {
		if (matches == null) {
			matches = db.getMatches(startRow, numDisplayedRows, playerName, gameName, tournamentID, true);
		}
		return matches;
	}
	
	@Override
	protected int getRowCount() throws SQLException {
		return db.getRowCountMatches(playerName, gameName, tournamentID, true);
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	
	public String getTournamentID() {
		return tournamentID;
	}

	public void setTournamentID(String tournamentID) {
		this.tournamentID = tournamentID;
	}

	@Override
	public String getTableName() {
		return "matches";
	}

	@Override
	public String getTargetJsp() {
		if (playerName == null) {
			return "show_matches.jsp";
		}
		return "show_matches.jsp?playerName=" + playerName;
	}
}
