package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.naming.NamingException;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.RemotePlayerInfo;
import tud.ggpserver.datamodel.User;

public class EditPlayer {
	private final static AbstractDBConnector db = DBConnectorFactory.getDBConnector();
	private boolean correctlyUpdated = false;

	private User user = null;
	private RemotePlayerInfo player = null;
	
	private String host = "";
	private int port = 0;
	private String status = RemotePlayerInfo.STATUS_INACTIVE;
	
	private List<String> errorsHost = new LinkedList<String>();
	private List<String> errorsPort = new LinkedList<String>();


	public boolean isValid() throws NamingException, SQLException {
		errorsHost.clear();
		errorsPort.clear();
		
		if (!RemotePlayerInfo.legalStatus(status)) {
			// It's okay to just force a status here, because the only way that
			// this might have become corrupted is that the user tinkered
			// manually with the POST parameters. The user input is a dropdown list,   
			// and if the user used that, only legal options are possible.
			status = RemotePlayerInfo.STATUS_INACTIVE;
		}
		
		if (host.equals("")) {
			errorsHost.add("host must not be empty");
		}
		if (host.length() > 255) {
			errorsHost.add("host must not be longer than 255 characters");
		}
		if (!host.matches( "[a-zA-Z0-9._-]*" )) {
			errorsHost.add("host must only contain the following characters: a-z A-Z 0-9 . _ -");
			// underscore not actually legal following RFC 952 and RFC 1123, but widely used
		}
		
		if (port < 0 || port > 65535) {
			errorsPort.add("port must be an integer between 0 and 65535 (inclusive)");
		}
		
		boolean result = true;
		
		if (errorsHost.size() > 0) {
			host = "";
			result = false;
		}
		if (errorsPort.size() > 0) {
			port = 0;
			result = false;
		}
		
		return result;
	}

	public void updatePlayer() throws NamingException, SQLException {
		assert (isValidPlayer());
		assert (isValid());
		
		db.updatePlayerInfo(getPlayerName(), getHost(), getPort(), user, getStatus());
		correctlyUpdated = true;
	}
	
	public boolean isCorrectlyUpdated() {
		return correctlyUpdated;
	}

	/**
	 * Checks if an existing user and player has been set and if the user has
	 * the right to edit it. This is done separately from host, port and status
	 * (these are checked in isValid()), because user and player are not really user
	 * input. If the user didn't manipulate the POST parameters manually, user and
	 * player will always be correctly set by the web application.
	 */
	public boolean isValidPlayer() {
		if (user == null || player == null) {
			return false;
		}
		if (!player.getOwner().equals(user)) {
			return false;
		}
		
		return true;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPlayerName() {
		if (player == null) {
			throw new IllegalStateException("player not set!");
		}
		return player.getName();
	}

	public void setPlayerName(String playerName) throws NamingException, SQLException {
		if (playerName.equals("Legal") || playerName.equals("Random")) {
			throw new IllegalArgumentException("player cannot be Legal or Random!");
		}

		// this cast is safe because of the check above
		RemotePlayerInfo player = (RemotePlayerInfo) db.getPlayerInfo(playerName);
		
		if (this.player == null) {
			host = player.getHost();
			port = player.getPort();
			status = player.getStatus();
		}
		this.player = player;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUserName() {
		if (user == null) {
			throw new IllegalStateException("user not set!");
		}
		return user.getUserName();
	}

	public void setUserName(String userName) throws NamingException, SQLException {
		user = db.getUser(userName);
	}

	public List<String> getErrorsHost() {
		return errorsHost;
	}

	public List<String> getErrorsPort() {
		return errorsPort;
	}
}
