/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de>
     				   Stephan Schiffel <stephan.schiffel@gmx.de>

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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.RemotePlayerInfo;
import tud.ggpserver.datamodel.matches.FinishedMatch;
import tud.ggpserver.datamodel.matches.ServerMatch;
import tud.ggpserver.util.Utilities;

/**
 * Keeps track of errors a player makes and disables players making too many errors. 
 */
public class PlayerErrorTracker<TermType extends TermInterface, ReasonerStateInfoType> implements PlayerStatusListener {
	private static final Logger logger = Logger.getLogger(PlayerErrorTracker.class.getName());
	private static final int MAX_ERROR_MATCHES = 2;

	private final Map<String, Integer> numOfflineMatches = Collections.synchronizedMap(new HashMap<String, Integer>());

	private final AbstractDBConnector<TermType, ReasonerStateInfoType> dbConnector;
	
	public PlayerErrorTracker(final AbstractDBConnector<TermType, ReasonerStateInfoType> dbConnector) {
		this.dbConnector = dbConnector;
		dbConnector.addPlayerStatusListener(this);
	}
	
	/* (non-Javadoc)
	 * @see tud.ggpserver.datamodel.PlayerStatusListener#notifyStatusChange(tud.ggpserver.datamodel.RemotePlayerInfo)
	 */
	@Override
	public void notifyStatusChange(RemotePlayerInfo player) {
		if (player.getStatus().equals(RemotePlayerInfo.STATUS_ACTIVE)) {
			// reset number of offline matches in a row so that the player starts fresh when it is re-enabled  
			numOfflineMatches.put(player.getName(), 0);
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
	public void updateDeadPlayers(FinishedMatch<TermType, ReasonerStateInfoType> match) {
		for (RemotePlayerInfo playerInfo : onlyRemotePlayerInfos(match)) {
			if (returnedOnlyErrors(match, playerInfo)) {
				int newNumOfflineMatches = getNumOfflineMatches(playerInfo.getName()) + 1;
				
				if (newNumOfflineMatches > MAX_ERROR_MATCHES) {
					disablePlayer(playerInfo);					
					addDisableMessage(playerInfo);
				} else {
					// increment number of offline matches in a row 
					numOfflineMatches.put(playerInfo.getName(), newNumOfflineMatches);
				}
			} else {
				// reset number of offline matches in a row 
				numOfflineMatches.put(playerInfo.getName(), 0);
			}
		}
	}

	private Collection<RemotePlayerInfo> onlyRemotePlayerInfos(ServerMatch<TermType, ReasonerStateInfoType> match) {
		Collection<RemotePlayerInfo> remotePlayerInfos = new LinkedList<RemotePlayerInfo>();
		
		for (PlayerInfo info : match.getPlayerInfos()) {
			if (info instanceof RemotePlayerInfo) {
				remotePlayerInfos.add((RemotePlayerInfo) info);
			}
		}
		return remotePlayerInfos;
	}

	private Integer getNumOfflineMatches(String playerName) {
		Integer result = numOfflineMatches.get(playerName);
		if (result == null) {
			result = 0;
		}
		return result;
	}

	/**
	 * @return <code>true</code> iff the given player caused an error message
	 *         in every single state of the given match, except for the terminal state.
	 */
	private boolean returnedOnlyErrors(FinishedMatch<TermType, ReasonerStateInfoType> match, PlayerInfo playerInfo) {
		if (match.getStatus() != ServerMatch.STATUS_FINISHED)
			return false;
	
		List<List<GameControllerErrorMessage>> errorMessages = match.getErrorMessagesForPlayer(playerInfo);
		assert (errorMessages.size() == match.getStringStates().size());
		Iterator<List<GameControllerErrorMessage>> i = errorMessages.iterator();
		while (i.hasNext()) {
			List<GameControllerErrorMessage> l = i.next();
			if(l.isEmpty() && i.hasNext()) {
				// no error messages for this state and the state is not the terminal one (i.hasNext())
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
					RemotePlayerInfo.STATUS_INACTIVE,
					playerInfo.isAvailableForRoundRobinMatches(),
					playerInfo.isAvailableForManualMatches(),
					Utilities.gdlVersion(playerInfo.getGdlVersion()));
		} catch (SQLException e) {
			logger.severe("exception: " + e);
		}
	}

	/**
	 * add an informative error message to the match
	 */
	private void addDisableMessage(RemotePlayerInfo playerInfo) {
		String message = "The status of player "
				+ playerInfo.getName()
				+ " was set to INACTIVE because it produced"
				+ " an error in each state of more than "
				+ MAX_ERROR_MATCHES + " matches in a row.";
//		GameControllerErrorMessage errorMessage = new GameControllerErrorMessage(
//						GameControllerErrorMessage.PLAYER_DISABLED,
//						message, playerInfo.getName());
//		match.notifyErrorMessage(errorMessage);
		logger.warning(message);
	}
	
	private AbstractDBConnector<TermType, ReasonerStateInfoType> getDBConnector() {
		return dbConnector;
	}

}
