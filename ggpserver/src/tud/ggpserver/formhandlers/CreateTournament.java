package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.DuplicateInstanceException;

public class CreateTournament {
	private String tournamentID = "";
	private String userName = "";
	private List<String> errors = new LinkedList<String>();
	
	private boolean correctlyCreated = false;
	
	public boolean isValid() throws SQLException {
		errors.clear();
		
		if (tournamentID.equals("")) {
			errors.add("tournament ID must not be empty");
		}
		if (tournamentID.length() > 40) {
			errors.add("tournament ID must not be longer than 40 characters");
		}
		if (!tournamentID.matches("[a-zA-Z0-9._-]*")) {
			errors.add("tournament ID must only contain the following characters: a-z A-Z 0-9 . _ -");
		} else if (DBConnectorFactory.getDBConnector().getTournament(tournamentID) != null) {
			// this is an "else if" such that only valid user names are checked to prevent SQL injection
			errors.add("tournament ID already exists, please pick a different one");
		}
		
		if (errors.size() > 0) {
			tournamentID = "";
			return false;
		}
		
		return true;
	}
	
	public void createTournament() throws SQLException {
		try {
			DBConnectorFactory.getDBConnector().createTournament(tournamentID, DBConnectorFactory.getDBConnector().getUser(userName));
			correctlyCreated = true;
		} catch (DuplicateInstanceException e) {
			errors.add("tournament ID already exists, please pick a different one");
			correctlyCreated = false;
		}
	}
	
	public boolean isCorrectlyCreated() {
		return correctlyCreated;
	}

	public List<String> getErrors() {
		return errors;
	}

	public String getTournamentID() {
		return tournamentID;
	}

	public void setTournamentID(String tournamentID) {
		this.tournamentID = tournamentID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
