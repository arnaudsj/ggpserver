package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.Map;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;

public class SaveTournament {
	private String tournamentID;

	private static final AbstractDBConnector<?, ?> db = DBConnectorFactory.getDBConnector();
	
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
	}
	
	private void parseTournamentID(String[] params) {
		if (params.length == 1) {
			setTournamentID(params[0]);
		}
	}

	private void parseGameName(String matchID, String[] params) throws SQLException {
		if (params.length == 1) {
			// TODO: db.getMatch(matchID).setGame(db.getGame(params[0]));
			System.out.println("match(" + matchID + ").setGame(" + params[0] + ")");
		}
	}

	private void parseStartClock(String matchID, String[] params) throws SQLException {
		if (params.length == 1) {
			try {
				Integer startClock = Integer.parseInt(params[0]);
				// TODO: db.getMatch(matchID).setStartClock(startClock);
				System.out.println("match(" + matchID + ").setStartClock(" + startClock + ")");
			} catch (NumberFormatException e) {
				// ignore
			}
		}
	}

	private void parsePlayClock(String matchID, String[] params) throws SQLException {
		if (params.length == 1) {
			try {
				Integer playClock = Integer.parseInt(params[0]);
				if (playClock != null) {
					// TODO: db.getMatch(matchID).setPlayClock(playClock);
					System.out.println("match(" + matchID + ").setPlayClock(" + playClock + ")");
				}
			} catch (NumberFormatException e) {
				// ignore
			}
		}
	}

	private void parsePlayerInfos(String matchID, String[] params) throws SQLException {
		int roleNumber = 0;
		for (String playerName : params) {
			// TODO: db.getMatch(matchID).setPlayerInfo(roleNumber, db.getPlayerInfo(playerName));
			System.out.println("match(" + matchID + ").setPlayerInfo(" + roleNumber + ", " + playerName + ")"); 
			
			roleNumber++;
		}
	}
}
