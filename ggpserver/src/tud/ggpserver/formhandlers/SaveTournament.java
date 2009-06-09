package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;


public class SaveTournament {
	private String tournamentID;

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
				String matchID = key.substring(9);
				parseGameName(matchID, params);
			} else if (key.startsWith("startclock+")) {
				String matchID = key.substring(11);
				parseStartClock(matchID, params);
			} else if (key.startsWith("playclock+")) {
				String matchID = key.substring(10);
				parsePlayClock(matchID, params);
			} else if (key.startsWith("playerInfos+")) {
				String matchID = key.substring(12);
				parsePlayerInfos(matchID, params);
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
}
