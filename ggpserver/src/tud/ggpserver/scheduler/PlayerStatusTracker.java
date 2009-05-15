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

package tud.ggpserver.scheduler;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.Match;
import tud.ggpserver.datamodel.RemotePlayerInfo;

/**
 * Keeps track of active / inactive players.
 * 
 * @author Martin Günther <mintar@gmx.de>
 * 
 */
public class PlayerStatusTracker<TermType extends TermInterface, ReasonerStateInfoType> implements PlayerStatusListener<TermType, ReasonerStateInfoType> {
	private static final Logger logger = Logger.getLogger(PlayerStatusTracker.class.getName());
	private static final int MAX_ERROR_MATCHES = 2;

	private final Map<RemotePlayerInfo, Integer> numOfflineMatches = Collections
			.synchronizedMap(new HashMap<RemotePlayerInfo, Integer>());

	private final AbstractDBConnector<TermType, ReasonerStateInfoType> dbConnector;
	private Set<RemotePlayerInfo> activePlayers;

	
	public PlayerStatusTracker(final AbstractDBConnector<TermType, ReasonerStateInfoType> dbConnector) {
		this.dbConnector = dbConnector;
		dbConnector.addPlayerStatusListener(this);
		
		try {
			activePlayers = new HashSet<RemotePlayerInfo>(getDBConnector()
					.getPlayerInfos(RemotePlayerInfo.STATUS_ACTIVE));
		} catch (SQLException e) {
			logger.severe("exception: " + e);
			InternalError internalError = new InternalError();
			internalError.initCause(e);
			throw internalError;
		}
	}
	
	/**
	 * Returns a list of active players. Doesn't block if there is no active player.
	 */
	public Collection<RemotePlayerInfo> getActivePlayers() {
	    synchronized (activePlayers) {
			// return a copy, so that the internal representation is not compromised
			return new LinkedList<RemotePlayerInfo>(activePlayers);
	    }
	}
	
	/**
	 * Returns a list of active players. Blocks until there is at least one active player.
	 */
	public Collection<RemotePlayerInfo> waitForActivePlayers() throws InterruptedException {
	    synchronized (activePlayers) {
			while (activePlayers.isEmpty()) {
				activePlayers.wait();
			}
			return new LinkedList<RemotePlayerInfo>(activePlayers);
		}
	}
	
	/* (non-Javadoc)
	 * @see tud.ggpserver.datamodel.PlayerStatusListener#notifyStatusChange(tud.ggpserver.datamodel.RemotePlayerInfo)
	 */
	@Override
	public void notifyStatusChange(RemotePlayerInfo player) {
		synchronized (activePlayers) {
			if (player.getStatus().equals(RemotePlayerInfo.STATUS_ACTIVE)) {
				activePlayers.add(player);   // since activePlayers is a set, we don't need to worry about duplicates
			} else if (player.getStatus().equals(RemotePlayerInfo.STATUS_INACTIVE)) {
				activePlayers.remove(player);
			}
			// reset number of offline matches in a row so that the player starts fresh when it is re-enabled  
			numOfflineMatches.put(player, 0);

			activePlayers.notify();
		}
	}

	/**
	 * For each player, the number of matches where the player produced any
	 * error ON EACH STATE is recorded here. If there is at least one state in
	 * the game without an error, the match does not count as an "error match".
	 * Also, only matches in a row are counted, i.e. whenever the player plays a
	 * non-error match, this number is reset to "0".<br>
	 * 
	 * When a player has played MAX_ERROR_MATCHES in a row, its status is set to
	 * "inactive".
	 */
	public void updateDeadPlayers(Match<TermType, ReasonerStateInfoType> match) {
		for (RemotePlayerInfo playerInfo : onlyRemotePlayerInfos(match)) {
			if (returnedOnlyErrors(match, playerInfo)) {
				int newNumOfflineMatches = getNumOfflineMatches(playerInfo) + 1;
				
				if (newNumOfflineMatches > MAX_ERROR_MATCHES) {
					disablePlayer(playerInfo);					
					addDisableMessage(match, playerInfo);
				} else {
					// increment number of offline matches in a row 
					numOfflineMatches.put(playerInfo, newNumOfflineMatches);
				}
			} else {
				// reset number of offline matches in a row 
				numOfflineMatches.put(playerInfo, 0);
			}
		}
	}

	private Collection<RemotePlayerInfo> onlyRemotePlayerInfos(Match<TermType, ReasonerStateInfoType> match) {
		Collection<RemotePlayerInfo> remotePlayerInfos = new LinkedList<RemotePlayerInfo>();
		
		for (PlayerInfo info : match.getPlayerInfos()) {
			if (info instanceof RemotePlayerInfo) {
				remotePlayerInfos.add((RemotePlayerInfo) info);
			}
		}
		return remotePlayerInfos;
	}

	private Integer getNumOfflineMatches(RemotePlayerInfo playerInfo) {
		Integer result = numOfflineMatches.get(playerInfo);
		if (result == null) {
			result = 0;
		}
		return result;
	}

	/**
	 * @return <code>true</code> iff the given player caused an error message
	 *         in every single state of the given match.
	 */
	private boolean returnedOnlyErrors(Match<TermType, ReasonerStateInfoType> match, PlayerInfo playerInfo) {
		List<List<GameControllerErrorMessage>> errorMessages = match.getErrorMessagesForPlayer(playerInfo);
		
		assert (errorMessages.size() == match.getNumberOfStates());
		
		for (int i = 0; i < match.getNumberOfStates(); i++) {
			if (errorMessages.get(i).size() == 0) {
				// no error messages for this state
				return false;
			}
		}
		return true;
	}
	
	/**
	 * update player status --> inactive
	 */
	private void disablePlayer(RemotePlayerInfo playerInfo) {
		try {
			getDBConnector().updatePlayerInfo(playerInfo.getName(),
					playerInfo.getHost(), playerInfo.getPort(),
					playerInfo.getOwner(),
					RemotePlayerInfo.STATUS_INACTIVE);
		} catch (SQLException e) {
			logger.severe("exception: " + e);
		}
	}

	/**
	 * add an informative error message to the match
	 */
	private void addDisableMessage(Match<TermType, ReasonerStateInfoType> match, RemotePlayerInfo playerInfo) {
		String message = "The status of player "
				+ playerInfo.getName()
				+ " was set to INACTIVE because it produced"
				+ " an error in each state of more than "
				+ MAX_ERROR_MATCHES + " matches in a row.";
		match.updateErrorMessage(new GameControllerErrorMessage(
				GameControllerErrorMessage.PLAYER_DISABLED,
				message, match, playerInfo.getName()));					
		logger.warning(message);
	}
	
	private AbstractDBConnector<TermType, ReasonerStateInfoType> getDBConnector() {
		return dbConnector;
	}
}
