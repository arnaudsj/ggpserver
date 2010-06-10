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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.Iterator;

import tud.gamecontroller.auxiliary.Pair;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.HumanPlayerInfo;
import tud.ggpserver.datamodel.RemoteOrHumanPlayerInfo;
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

	private Map<String, RemoteOrHumanPlayerInfo> activePlayers;
	private Set<String> playingPlayers;

	private Collection<AvailablePlayersListener> availablePlayersListeners = new ArrayList<AvailablePlayersListener>();
	
	private Set<Pair<String,String>> availableHumanPlayers;
	
	
	public AvailablePlayersTracker(final AbstractDBConnector<TermType, ReasonerStateInfoType> dbConnector) {
		logger.info("initializing AvailablePlayersTracker ...");
		activePlayers = new HashMap<String, RemoteOrHumanPlayerInfo>();
		playingPlayers = Collections.synchronizedSet(new HashSet<String>());
		availableHumanPlayers = Collections.synchronizedSet(new HashSet<Pair<String,String>>());
		
		dbConnector.addPlayerStatusListener(this);
		synchronized (this) {
			try {
				// add active remote players
				for(RemotePlayerInfo p:dbConnector.getPlayerInfos(RemoteOrHumanPlayerInfo.STATUS_ACTIVE)) {
					activePlayers.put(p.getName(), p);
				}
				// in the constructor, we don't care about HumanPlayers yet
			} catch (SQLException e) {
				logger.severe("exception: " + e);
				InternalError internalError = new InternalError();
				internalError.initCause(e);
				throw internalError;
			}
		}
	}

	public void addAvailablePlayersListener(AvailablePlayersListener l) {
		synchronized (availablePlayersListeners) {
			availablePlayersListeners.add(l);
		}
	}

	public void removeAvailablePlayersListener(AvailablePlayersListener l) {
		synchronized (availablePlayersListeners) {
			availablePlayersListeners.remove(l);
		}
	}
	
	private void notifyAvailablePlayersListeners(RemoteOrHumanPlayerInfo player) {
		synchronized(availablePlayersListeners) {
			for(AvailablePlayersListener l:availablePlayersListeners) {
				logger.info("notify listener " + l + " about player " + player.getName());
				l.notifyAvailable(player);
			}
		}
	}

	/**
	 * Returns a list of active players. Doesn't block if there is no active player.
	 */
	public synchronized Collection<RemoteOrHumanPlayerInfo> getActivePlayers() {
		// return a copy, so that the internal representation is not compromised
		return new LinkedList<RemoteOrHumanPlayerInfo>(activePlayers.values());
	}
	
	public synchronized void setAccepted (String matchID, String playerName) {
		Pair<String,String> p = new Pair<String, String>(matchID, playerName);
		this.availableHumanPlayers.add(p);
		logger.info("Enabling pair <"+p.getLeft()+" ; "+p.getRight()+">");
	}
	
	/**
	 * removes all info about availability of HumanPlayers for the given matchID
	 * @param matchID
	 */
	public synchronized void forgetAboutHumansAvailability (String matchID) {
		Iterator<Pair<String,String>> it = availableHumanPlayers.iterator();
		while (it.hasNext()) {
			Pair<String,String> p = it.next();
			if (p.getLeft().equals(matchID))
				it.remove();
		}
	}
	
	// only for HumanPlayers
	public synchronized boolean hasAccepted (String matchID, String playerName) {
		return availableHumanPlayers.contains(new Pair<String,String>(matchID, playerName));
	}

	/**
	 * Returns a list of active players. Blocks until there is at least one active player.
	 */
	public synchronized Collection<RemoteOrHumanPlayerInfo> waitForActivePlayers() throws InterruptedException {
		while (activePlayers.isEmpty()) {
			this.wait();
		}
		return new LinkedList<RemoteOrHumanPlayerInfo>(activePlayers.values());
	}

	/* (non-Javadoc)
	 * @see tud.ggpserver.datamodel.PlayerStatusListener#notifyStatusChange(tud.ggpserver.datamodel.RemotePlayerInfo)
	 */
	@Override
	public synchronized void notifyStatusChange(RemotePlayerInfo player) {
		logger.info("notifyStatusChange called:" + player);
		if (player.getStatus().equals(RemoteOrHumanPlayerInfo.STATUS_ACTIVE)) {
			logger.info("player " + player.getName() + " is now active");
			activePlayers.put(player.getName(), player);   // since activePlayers is a set, we don't need to worry about duplicates
			// it is also necessary to store the new player here in case the address has changed
			this.notifyAll();
			notifyAvailablePlayersListeners(player);
		} else if (player.getStatus().equals(RemoteOrHumanPlayerInfo.STATUS_INACTIVE)) {
			logger.info("player " + player.getName() + " is now inactive");
			if (activePlayers.containsKey(player.getName())) // it could be that this player was not GDL-compatible, and therefore not in 'activePlayers'
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
		logger.info(""+playingPlayers+".contains("+name+") = "+playingPlayers.contains(name));
		logger.info(""+activePlayers+".containsKey("+name+") = "+activePlayers.containsKey(name));
		return !playingPlayers.contains(name) && activePlayers.containsKey(name);
	}

	public synchronized Collection<? extends PlayerInfo> waitForPlayersAvailableForRoundRobin() throws InterruptedException {
		Set<PlayerInfo> availablePlayerSet = new HashSet<PlayerInfo>();
		while(availablePlayerSet.isEmpty()) {
			Collection<RemoteOrHumanPlayerInfo> activePlayers = waitForActivePlayers();
			for(RemoteOrHumanPlayerInfo player:activePlayers) {
				if (player instanceof RemotePlayerInfo) {
					if(		((RemotePlayerInfo)player).isAvailableForRoundRobinMatches() &&
							!isPlaying(player.getName()) ) { // TODO: only take compatible player
						availablePlayerSet.add((RemotePlayerInfo)player);
					}
				}
			}
			if(availablePlayerSet.isEmpty()) {
				this.wait();
			}
		}
		return availablePlayerSet;
	}

}
