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
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import tud.ggpserver.datamodel.DBConnector;
import tud.ggpserver.datamodel.User;
import tud.ggpserver.datamodel.matches.ServerMatch;

public class SaveTournament {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(SaveTournament.class.getName());

	/**
	 * Full parameter names
	 */
	public static final String PARAM_TOURNAMENT_ID = "tournamentID";
	public static final String PARAM_SUBMIT_BUTTON = "submitButton";

	/**
	 * Parameter prefixes (the full parameter name consists of a prefix + match id).
	 */
	public static final String PREFIX_GAME_NAME = "gameName+";
	public static final String PREFIX_PLAYER_INFOS = "playerInfos+";
	public static final String PREFIX_PLAY_CLOCK = "playclock+";
	public static final String PREFIX_START_CLOCK = "startclock+";
	public static final String PREFIX_SCRAMBLED = "scrambled+";
	public static final String PREFIX_GOALVALUE = "goalvalue+";
	public static final String PREFIX_WEIGHT = "weight+";

	private String tournamentID;
	private String errorString = "";
	private int page;
	private boolean correctlyPerformed = false;
	private boolean newContent = true;
	private User user = null;
	
	private Map<String, EditableMatch> editableMatches = new HashMap<String, EditableMatch>();

	public void setUserName(String userName) throws SQLException {
		user = DBConnector.getInstance().getUser(userName);
	}
	
	public boolean isAllow(String matchid) throws SQLException {
		DBConnector db = DBConnector.getInstance();
		ServerMatch<?, ?> match = db.getMatch(matchid);
		if (match == null || user == null)
			return false;
		
		if (match.getOwner().equals(user))
			return true;
		
		if (user.isAdmin())
			return true;
		
		errorString = "user '"+user.getUserName()+"' is not allowed to edit match '"+matchid+"'";
		
		logger.warning(errorString);
		return false;
	}
	
	public String getErrorString() {
		return errorString;
	}
	
	public void setTournamentID(String tournamentID) {
		this.tournamentID = tournamentID;
		logger.config("String - tournamentID: " + tournamentID); //$NON-NLS-1$
	}
	
	public void setNewContent(boolean newContent) {
		this.newContent = newContent;
	}
	
	public boolean isNewContent() {
		return newContent;
	}
	
	public String getTournamentID() {
		return tournamentID;
	}
	
	public boolean isCorrectlyPerformed() {
		return correctlyPerformed;
	}
	

	public void parseParameterMap(Map<String, String[]> parameterMap) throws SQLException {
		try {
			for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
				String key = entry.getKey();
				String[] params = entry.getValue();
				if (key.equals(PARAM_TOURNAMENT_ID)) {
					parseTournamentID(params);
				} else if (key.equals(PARAM_SUBMIT_BUTTON)) {
					// ignore
				} else if (key.startsWith(PREFIX_GAME_NAME)) {
					parseGameName(key.substring(PREFIX_GAME_NAME.length()), params);
				} else if (key.startsWith(PREFIX_START_CLOCK)) {
					parseStartClock(key.substring(PREFIX_START_CLOCK.length()), params);
				} else if (key.startsWith(PREFIX_PLAY_CLOCK)) {
					parsePlayClock(key.substring(PREFIX_PLAY_CLOCK.length()), params);
				} else if (key.startsWith(PREFIX_PLAYER_INFOS)) {
					parsePlayerInfos(key.substring(PREFIX_PLAYER_INFOS.length()), params);
				} else if (key.startsWith(PREFIX_SCRAMBLED)) {
					parseScrambled(key.substring(PREFIX_SCRAMBLED.length()), params);
				} else if (key.startsWith(PREFIX_GOALVALUE)) {
					parseGoalValue(key.substring(PREFIX_GOALVALUE.length()), params);
				} else if (key.startsWith(PREFIX_WEIGHT)) {
					parseWeight(key.substring(PREFIX_WEIGHT.length()), params);
				} else if (key.equals("newContent") || key.equals("page")) {
					// ignore these values
				} else {
					logger.warning("Unknown parameter: " + key + " (values " + Arrays.toString(params) + ")");
				}
			}
			
			commit();
			correctlyPerformed = true;
		} catch (IllegalArgumentException e) {
			errorString = e.getMessage();
			correctlyPerformed = false;
		} catch (IllegalStateException e) {
			errorString = e.getMessage();
			correctlyPerformed = false;
		}
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

	private void parseWeight(String matchID, String[] params) throws SQLException {
		if (params.length == 1) {
			try {
				Double weight = Double.parseDouble(params[0]);
				if (weight != null) {
					getEditableMatch(matchID).setWeight(weight);
					logger.config("String, String[] - match(" + matchID + ").setWeight(" + weight + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
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

	private void parseScrambled(String matchID, String[] params) throws SQLException {
		if (params.length == 1 && params[0].equals("checked")) {
			getEditableMatch(matchID).setScrambled(true);
			logger.config("String, String[] - match(" + matchID + ").setScrambled(" + true + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	private void parseGoalValue(String matchID, String[] params) throws SQLException {
		int roleNumber = 0;
		for (String param : params) {
			int goalValue = Integer.parseInt(param);
			getEditableMatch(matchID).setGoalValue(roleNumber, goalValue);
			logger.config("String, String[] - match(" + matchID + ").setGoalValue(" + roleNumber + ", " + goalValue + ")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			roleNumber++;
		}
	}

	private EditableMatch getEditableMatch(String matchID) throws SQLException {
		EditableMatch result = editableMatches.get(matchID);
		if (result == null) {
			if (isAllow(matchID)) {
				result = new EditableMatch(matchID);
				editableMatches.put(matchID, result);
			} else
				throw new IllegalStateException("You are not allowed to edit this match!");
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
