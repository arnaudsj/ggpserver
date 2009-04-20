package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.naming.NamingException;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.DuplicateInstanceException;
import tud.ggpserver.datamodel.RemotePlayerInfo;

public class CreatePlayer {
	private String playerName = "";
	private String userName = "";
	private List<String> errors = new LinkedList<String>();
	
	private boolean correctlyCreated = false;
	
	private final static AbstractDBConnector db = DBConnectorFactory.getDBConnector();

	public boolean isValid() throws NamingException, SQLException {
		errors.clear();
		
		if (playerName.equals("")) {
			errors.add("player name must not be empty");
		}
		if (playerName.equalsIgnoreCase("Legal")) {
			errors.add("player name can not be \"Legal\"");
		}
		if (playerName.equalsIgnoreCase("Random")) {
			errors.add("player name can not be \"Random\"");
		}
		if (playerName.length() > 20) {
			errors.add("player name must not be longer than 20 characters");
		}
		if (!playerName.matches( "[a-zA-Z][a-zA-Z0-9._-]*" )) {
			errors.add("player name must begin with a letter and only contain the following characters: a-z A-Z 0-9 . _ -");
			// do NOT allow "<" or ">" for the user name (otherwise cross-site scripting possible)
		} else if (db.getPlayerInfo(playerName) != null) {
			// this is an "else if" such that only valid user names are checked to prevent SQL injection
			errors.add("player name already exists, please pick a different one");
		}
		
		if (errors.size() > 0) {
			playerName = "";
			return false;
		}
		
		return true;
	}
	
	public void createPlayer() throws NamingException, SQLException {
		try {
			db.createPlayerInfo(playerName, "", 0, db.getUser(userName), RemotePlayerInfo.STATUS_NEW);
			correctlyCreated = true;
		} catch (DuplicateInstanceException e) {
			errors.add("player name already exists, please pick a different one");
			correctlyCreated = false;
		}
	}
	
	public boolean isCorrectlyCreated() {
		return correctlyCreated;
	}

	public List<String> getErrors() {
		return errors;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}
}
