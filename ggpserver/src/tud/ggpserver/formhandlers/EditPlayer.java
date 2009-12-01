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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.RemotePlayerInfo;
import tud.ggpserver.datamodel.User;

public class EditPlayer {
	private boolean correctlyUpdated = false;

	private User user = null;
	private RemotePlayerInfo player = null;
	
	private String host = "";
	private int port = 0;
	private String status = RemotePlayerInfo.STATUS_INACTIVE;
	private boolean availableForRoundRobinMatches = false;
	private boolean availableForManualMatches = false;
	
	private List<String> errorsHost = new LinkedList<String>();
	private List<String> errorsPort = new LinkedList<String>();


	public boolean isValid() {
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
		
		try {
			InetAddress hostAddress = InetAddress.getByName(host);
			// TODO: make this check an option (incase one wants to run the server in a local network)
			if (hostAddress.isSiteLocalAddress()) {
				// 10.0.0.0    - 10.255.255.255  (10/8 prefix)
				// 172.16.0.0  - 172.31.255.255  (172.16/12 prefix)
				// 192.168.0.0 - 192.168.255.255 (192.168/16 prefix)
				errorsHost.add("private ip addresses not allowed for security reasons");
			} else if (hostAddress.isAnyLocalAddress()) {
				// 0.0.0.0
				errorsHost.add("wildcard ip address not allowed for security reasons");
			} else if (hostAddress.isLinkLocalAddress()) {
				// 169.254.0.0 - 169.254.255.255 (169.254/16 prefix)
				errorsHost.add("link-local ip addresses not allowed for security reasons");
			} else if (hostAddress.isLoopbackAddress()) {
				// 127.0.0.0 - 127.255.255.255 (127/8 prefix)
				// e.g. 127.0.0.1, "localhost", "thales" (when run on host thales)
				errorsHost.add("loopback ip addresses not allowed for security reasons");
			} else if (hostAddress.isMulticastAddress()) {
				// 224.0.0.0 - 239.255.255.255
				errorsHost.add("multicast ip addresses not allowed for security reasons");
			}
		} catch (UnknownHostException e) {
			errorsHost.add("DNS error, unknown host: the IP address of this host could not be determined");
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

	public void updatePlayer() throws SQLException {
		assert (isValidPlayer());
		assert (isValid());
		
		DBConnectorFactory.getDBConnector().updatePlayerInfo(getPlayerName(), getHost(), getPort(), user, getStatus(), availableForRoundRobinMatches, availableForManualMatches);
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

	public void setPlayerName(String playerName) throws SQLException {
		if (playerName.equals(AbstractDBConnector.PLAYER_RANDOM) || playerName.equals(AbstractDBConnector.PLAYER_RANDOM)) {
			throw new IllegalArgumentException("player cannot be " + AbstractDBConnector.PLAYER_RANDOM + " or " + AbstractDBConnector.PLAYER_LEGAL + "!");
		}

		// this cast is safe because of the check above
		RemotePlayerInfo player = (RemotePlayerInfo) DBConnectorFactory.getDBConnector().getPlayerInfo(playerName);
		
		if (this.player == null) {
			host = player.getHost();
			port = player.getPort();
			status = player.getStatus();
			availableForRoundRobinMatches = player.isAvailableForRoundRobinMatches();
			availableForManualMatches = player.isAvailableForManualMatches();
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

	public void setUserName(String userName) throws SQLException {
		user = DBConnectorFactory.getDBConnector().getUser(userName);
	}

	public boolean isAvailableForRoundRobinMatches() {
		return availableForRoundRobinMatches;
	}

	public void setAvailableForRoundRobinMatches(
			boolean availableForRoundRobinMatches) {
		this.availableForRoundRobinMatches = availableForRoundRobinMatches;
	}

	public boolean isAvailableForManualMatches() {
		return availableForManualMatches;
	}

	public void setAvailableForManualMatches(boolean availableForManualMatches) {
		this.availableForManualMatches = availableForManualMatches;
	}

	public List<String> getErrorsHost() {
		return errorsHost;
	}

	public List<String> getErrorsPort() {
		return errorsPort;
	}
}
