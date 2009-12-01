/*
Copyright (C) 2009 Stephan Schiffel <stephan.schiffel@gmx.de>

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
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.RemotePlayerInfo;

/**
 * Keeps track of active / inactive players and of players currently playing a match.
 * <br/>
 * Active/inactive means the player takes part in the round_robin_tournament or not,
 * playing means the player is currently playing some match and available means the player
 * is active and not currently playing a match.
 * <br/>
 * There are ??? states a player can be in, associated with the player belonging to certain sets or not:
 * <dl>
 *		<dt>inactive, not playing</dt>
 *		<dd>not in activePlayers, not in playingPlayers</dd>
 *
 *		<dt>active, not playing</dt>
 *		<dd>in activePlayers, not in playingPlayers -&gt; player is available</dd>
 *
 *		<dt>inactive, playing (may happen in case of manually scheduled matches)</dt>
 *		<dd>not in activePlayers, in playingPlayers</dd>
 *
 *		<dt>active, playing</dt>
 *		<dd>in activePlayers, in playingPlayers</dd>
 * </dl>
 */

public class AvailablePlayersTracker<TermType extends TermInterface, ReasonerStateInfoType> implements PlayerStatusListener {
	private static final Logger logger = Logger.getLogger(AvailablePlayersTracker.class.getName());

	private Map<String, RemotePlayerInfo> activePlayers;
	private Set<String> playingPlayers;

	public AvailablePlayersTracker(final AbstractDBConnector<TermType, ReasonerStateInfoType> dbConnector) {
		activePlayers = new HashMap<String, RemotePlayerInfo>();
		playingPlayers = Collections.synchronizedSet(new HashSet<String>());
		dbConnector.addPlayerStatusListener(this);
		synchronized (this) {
			try {
				for(RemotePlayerInfo p:dbConnector.getPlayerInfos(RemotePlayerInfo.STATUS_ACTIVE)) {
					activePlayers.put(p.getName(), p);
				}
			} catch (SQLException e) {
				logger.severe("exception: " + e);
				InternalError internalError = new InternalError();
				internalError.initCause(e);
				throw internalError;
			}
		}
	}

	/**
	 * Returns a list of active players. Doesn't block if there is no active player.
	 */
	public synchronized Collection<RemotePlayerInfo> getActivePlayers() {
		// return a copy, so that the internal representation is not compromised
		return new LinkedList<RemotePlayerInfo>(activePlayers.values());
	}

	/**
	 * Returns a list of active players. Blocks until there is at least one active player.
	 */
	public synchronized Collection<RemotePlayerInfo> waitForActivePlayers() throws InterruptedException {
		while (activePlayers.isEmpty()) {
			this.wait();
		}
		return new LinkedList<RemotePlayerInfo>(activePlayers.values());
	}

	/* (non-Javadoc)
	 * @see tud.ggpserver.datamodel.PlayerStatusListener#notifyStatusChange(tud.ggpserver.datamodel.RemotePlayerInfo)
	 */
	@Override
	public synchronized void notifyStatusChange(RemotePlayerInfo player) {
		if (player.getStatus().equals(RemotePlayerInfo.STATUS_ACTIVE)) {
			activePlayers.put(player.getName(), player);   // since activePlayers is a set, we don't need to worry about duplicates
			this.notifyAll();
		} else if (player.getStatus().equals(RemotePlayerInfo.STATUS_INACTIVE)) {
			activePlayers.remove(player.getName());
			this.notifyAll();
		}
	}

	public synchronized void notifyStartPlaying(String name) {
		playingPlayers.add(name);
		this.notifyAll();
	}

	public synchronized void notifyStopPlaying(String name) {
		playingPlayers.remove(name);
		this.notifyAll();
	}

	public boolean isPlaying(String name) {
		return playingPlayers.contains(name);
	}

	public synchronized boolean isAvailable(String name) {
		return !playingPlayers.contains(name) && activePlayers.containsKey(name);
	}

	public synchronized Collection<RemotePlayerInfo> waitForPlayersAvailableForRoundRobin() throws InterruptedException {
		Set<RemotePlayerInfo> availablePlayerSet = new HashSet<RemotePlayerInfo>();
		while(availablePlayerSet.isEmpty()) {
			Collection<RemotePlayerInfo> activePlayers=waitForActivePlayers();
			for(RemotePlayerInfo player:activePlayers) {
				if(player.isAvailableForRoundRobinMatches() && !isPlaying(player.getName())) {
					availablePlayerSet.add(player);
				}
			}
			if(availablePlayerSet.isEmpty()) {
				this.wait();
			}
		}
		return availablePlayerSet;
	}


}
