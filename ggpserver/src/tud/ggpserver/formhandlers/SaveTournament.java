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
import java.util.HashMap;
import java.util.Map;


public class SaveTournament {
	private String tournamentID;
	private int page;

	private Map<String, EditableMatch> editableMatches = new HashMap<String, EditableMatch>();
	
	public void setTournamentID(String tournamentID) {
		this.tournamentID = tournamentID;
		System.out.println("tournamentID: " + tournamentID);
	}

	public String getTournamentID() {
		return tournamentID;
	}
	
	public void parseParameterMap(Map<String, String[]> parameterMap) throws SQLException {
		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String key = entry.getKey();
			String[] params = entry.getValue();
			if (key.equals("tournamentID")) {
				parseTournamentID(params);
			} else if (key.startsWith("gameName+")) {
				parseGameName(key.substring(9), params);
			} else if (key.startsWith("startclock+")) {
				parseStartClock(key.substring(11), params);
			} else if (key.startsWith("playclock+")) {
				parsePlayClock(key.substring(10), params);
			} else if (key.startsWith("playerInfos+")) {
				parsePlayerInfos(key.substring(12), params);
			} else {
				System.err.print("Unknown parameter: " + key + " (values ");
				for (String value : params) {
					System.out.print(value + " ");
				}
				System.out.println(")");
			}
		}
		commit();
	}
	
	private void commit() throws SQLException {
		for (EditableMatch match : editableMatches.values()) {
			match.commit();
		}
	}
	
	private void parseTournamentID(String[] params) {
		if (params.length == 1) {
			setTournamentID(params[0]);
		}
	}

	@SuppressWarnings("unchecked")
	private void parseGameName(String matchID, String[] params) throws SQLException {
		if (params.length == 1) {
			getEditableMatch(matchID).setGame(params[0]);
			System.out.println("match(" + matchID + ").setGame(" + params[0] + ")");
		}
	}

	private void parseStartClock(String matchID, String[] params) throws SQLException {
		if (params.length == 1) {
			try {
				Integer startclock = Integer.parseInt(params[0]);
				getEditableMatch(matchID).setStartclock(startclock);
				System.out.println("match(" + matchID + ").setStartClock(" + startclock + ")");
			} catch (NumberFormatException e) {
				// ignore
			}
		}
	}

	private void parsePlayClock(String matchID, String[] params) throws SQLException {
		if (params.length == 1) {
			try {
				Integer playclock = Integer.parseInt(params[0]);
				if (playclock != null) {
					getEditableMatch(matchID).setPlayclock(playclock);
					System.out.println("match(" + matchID + ").setPlayClock(" + playclock + ")");
				}
			} catch (NumberFormatException e) {
				// ignore
			}
		}
	}

	private void parsePlayerInfos(String matchID, String[] params) throws SQLException {
		int roleNumber = 0;
		for (String playerName : params) {
			getEditableMatch(matchID).setPlayerInfo(roleNumber, playerName);
			System.out.println("match(" + matchID + ").setPlayerInfo(" + roleNumber + ", " + playerName + ")"); 
			
			roleNumber++;
		}
	}
	
	private EditableMatch getEditableMatch(String matchID) throws SQLException {
		EditableMatch result = editableMatches.get(matchID);
		if (result == null) {
			result = new EditableMatch(matchID);
			editableMatches.put(matchID, result);
		}
		return result;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}
}
