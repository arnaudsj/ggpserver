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
import java.util.logging.Logger;


public class SaveTournament {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SaveTournament.class.getName());

	/**
	 * Full parameter names
	 */
	public static final String PARAM_TOURNAMENT_ID = "tournamentID";

	/**
	 * Parameter prefixes.
	 */
	public static final String PREFIX_GAME_NAME = "gameName+";
	public static final String PREFIX_PLAYER_INFOS = "playerInfos+";
	public static final String PREFIX_PLAY_CLOCK = "playclock+";
	public static final String PREFIX_START_CLOCK = "startclock+";

	private String tournamentID;
	private int page;

	private Map<String, EditableMatch> editableMatches = new HashMap<String, EditableMatch>();

	public void setTournamentID(String tournamentID) {
		this.tournamentID = tournamentID;
		logger.config("String - tournamentID: " + tournamentID); //$NON-NLS-1$
	}

	public String getTournamentID() {
		return tournamentID;
	}

	public void parseParameterMap(Map<String, String[]> parameterMap) throws SQLException {
		for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
			String key = entry.getKey();
			String[] params = entry.getValue();
			if (key.equals(PARAM_TOURNAMENT_ID)) {
				parseTournamentID(params);
			} else if (key.startsWith(PREFIX_GAME_NAME)) {
				parseGameName(key.substring(PREFIX_GAME_NAME.length()), params);
			} else if (key.startsWith(PREFIX_START_CLOCK)) {
				parseStartClock(key.substring(PREFIX_START_CLOCK.length()), params);
			} else if (key.startsWith(PREFIX_PLAY_CLOCK)) {
				parsePlayClock(key.substring(PREFIX_PLAY_CLOCK.length()), params);
			} else if (key.startsWith(PREFIX_PLAYER_INFOS)) {
				parsePlayerInfos(key.substring(PREFIX_PLAYER_INFOS.length()), params);
			} else {
				logger.warning("Map<String,String[]> - Unknown parameter: " + key + " (values ");
				for (String value : params) {
					logger.warning(value + " ");
				}
				logger.warning(")");
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
			logger.config("String, String[] - match(" + matchID + ").setGame(" + params[0] + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	private void parseStartClock(String matchID, String[] params) throws SQLException {
		if (params.length == 1) {
			try {
				Integer startclock = Integer.parseInt(params[0]);
				getEditableMatch(matchID).setStartclock(startclock);
				logger.config("String, String[] - match(" + matchID + ").setStartClock(" + startclock + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
					logger.config("String, String[] - match(" + matchID + ").setPlayClock(" + playclock + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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
			logger.config("String, String[] - match(" + matchID + ").setPlayerInfo(" + roleNumber + ", " + playerName + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

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
