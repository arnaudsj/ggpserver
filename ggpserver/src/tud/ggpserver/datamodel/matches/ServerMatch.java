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

package tud.ggpserver.datamodel.matches;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.Match;
import tud.gamecontroller.game.impl.State;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;

public abstract class ServerMatch<TermType extends TermInterface, ReasonerStateInfoType>
		extends Match<TermType, ReasonerStateInfoType> {

	public static final String STATUS_NEW = "new";
	public static final String STATUS_RUNNING = "running";
	public static final String STATUS_FINISHED = "finished";
	public static final String STATUS_ABORTED = "aborted";
	public static final String STATUS_SCHEDULED = "scheduled";
	
	private final Date startTime;
	private final Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> rolesToPlayerInfos;
	private List<PlayerInfo> orderedPlayerInfos = null;
	
	private List<Integer> orderedGoalValues;
	
	private final boolean scrambled;


	/**
	 * State 0 = initial state, State 1 = state after first joint move, ..., State n = final state
	 */
	protected List<String> xmlStates;
	
	protected List<List<String>> jointMovesStrings;
	
	/**
	 * - errors from the start message and first play message go to index 0
	 * - errors from the second play message go to index 1
	 * - ...
	 * - errors from the last (n^th) play message go to index n-1
	 * - errors from the stop message go to index n
	 * The match has n+1 states, n play messages and the stop message, therefore there is an entry in errorMessages for each state.
	 */
	protected List<List<GameControllerErrorMessage>> errorMessages;
	
	private final AbstractDBConnector<TermType, ReasonerStateInfoType> db;
	
	
	/**
	 * Don't use this constructor directly, use DBConnectorFactory.getDBConnector().getMatch() instead. 
	 */
	public ServerMatch(
			String matchID,
			GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game,
			int startclock,
			int playclock,
			Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> rolesToPlayerInfos,
			Date startTime,
			boolean scrambled,
			AbstractDBConnector<TermType, ReasonerStateInfoType> db) {
		super(matchID, game, startclock, playclock);
		this.rolesToPlayerInfos = rolesToPlayerInfos;
		this.startTime = new Date(startTime.getTime());
		this.scrambled = scrambled;
		this.db = db;
	}

	
	/////////////////////// player infos ///////////////////////
	
	public List<? extends PlayerInfo> getOrderedPlayerInfos() {
		if (orderedPlayerInfos == null) {
			orderedPlayerInfos = new LinkedList<PlayerInfo>();
			for (RoleInterface<TermType> role : getGame().getOrderedRoles()) {
				PlayerInfo playerInfo = rolesToPlayerInfos.get(role);
				assert (playerInfo != null);
				orderedPlayerInfos.add(playerInfo);
			}
		}
		return new ArrayList<PlayerInfo>(orderedPlayerInfos);   // defensive copy, needed e.g. in EditableMatch
	}

	public PlayerInfo getPlayerInfo(RoleInterface<TermType> role) {
		return rolesToPlayerInfos.get(role);
	}

	public Collection<? extends PlayerInfo> getPlayerInfos() {
		return new ArrayList<PlayerInfo>(rolesToPlayerInfos.values());
	}

	public Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> getRolesToPlayerInfos() {
		return new HashMap<RoleInterface<TermType>, PlayerInfo>(rolesToPlayerInfos);
	}

	/////////////////////// start time ///////////////////////

	public Date getStartTime() {
		return new Date(startTime.getTime());
	}

	/////////////////////// status ///////////////////////

	public abstract String getStatus();

	/////////////////////// goal values ///////////////////////
	
	/**
	 * This method can be overridden by subclasses to provide goal values. 
	 * 
	 * @return May return <code>null</code> if there are no goal values for
	 *         this match yet. <br>
	 */
	public Map<? extends RoleInterface<?>, Integer> getGoalValues() {
		return null;
	}

	/**
	 * @return May return <code>null</code> if there are no goal values for
	 *         this match yet. <br>
	 */
	public List<Integer> getOrderedGoalValues() {
		Map<? extends RoleInterface<?>, Integer> goalValues = getGoalValues();
		if (goalValues == null) {
			return null;
		}
		if (orderedGoalValues == null) {
			orderedGoalValues = new LinkedList<Integer>();
			for (RoleInterface<?> role : getGame().getOrderedRoles()) {
				orderedGoalValues.add(goalValues.get(role));
			}
		}
		return new ArrayList<Integer>(orderedGoalValues);
	}

	/////////////////////// joint moves strings ///////////////////////

	public abstract List<List<String>> getJointMovesStrings();

	/////////////////////// XML states ///////////////////////
	
	public abstract List<String> getXmlStates();

	/////////////////////// error messages ///////////////////////
	
	public abstract List<List<GameControllerErrorMessage>> getErrorMessages();
	
	public boolean getHasErrors() {
		for (List<GameControllerErrorMessage> errorMessagesSingleState : getErrorMessages()) {
			if (!errorMessagesSingleState.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public boolean getHasErrorsSinglePlayer(PlayerInfo player) {
		for (List<GameControllerErrorMessage> errorMessagesSingleState : getErrorMessagesForPlayer(player)) {
			if (!errorMessagesSingleState.isEmpty()) {
				return true;
			}
		}
		return false;
	}

	public Map<String, Boolean> getHasErrorsAllPlayers() {
		Map<String, Boolean> result = new HashMap<String, Boolean>();
		
		for (PlayerInfo player : getPlayerInfos()) {
			result.put(player.getName(), getHasErrorsSinglePlayer(player));
		}
		
		return result;
	}

	public List<List<GameControllerErrorMessage>> getErrorMessagesForPlayer(String playerName) {
		for (PlayerInfo playerInfo : getPlayerInfos()) {
			if (playerInfo.getName().equals(playerName)) {
				return getErrorMessagesForPlayer(playerInfo);
			}
		}
		throw new IllegalArgumentException("No player info for name " + playerName + " in match " + this);
	}

	public List<List<GameControllerErrorMessage>> getErrorMessagesForPlayer(PlayerInfo player) {
		if (!getPlayerInfos().contains(player)) {
			throw new IllegalArgumentException("Player info " + player + " not found in match " + this);
		}
		
		List<List<GameControllerErrorMessage>> result = new LinkedList<List<GameControllerErrorMessage>>();
		
		for (List<GameControllerErrorMessage> errorsSingleState : getErrorMessages()) {
			List<GameControllerErrorMessage> playerErrorsSingleState = new LinkedList<GameControllerErrorMessage>();
	
			for (GameControllerErrorMessage message : errorsSingleState) {
				if (player.getName().equals(message.getPlayerName())) {
					playerErrorsSingleState.add(message);
				}
			}
			result.add(playerErrorsSingleState);
		}
		return result;
	}

	
	/////////////////////// scrambling ///////////////////////
	
	public boolean isScrambled() {
		return scrambled;
	}
	
	/////////////////////// everything else ///////////////////////

	protected AbstractDBConnector<TermType, ReasonerStateInfoType> getDB() {
		return db;
	}


	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[ServerMatch:");
		buffer.append(" matchID: ");
		buffer.append(getMatchID());
		buffer.append(" game: ");
		buffer.append(getGame());
		buffer.append(" startclock: ");
		buffer.append(getStartclock());
		buffer.append(" playclock: ");
		buffer.append(getPlayclock());
		buffer.append(" startTime: ");
		buffer.append(startTime);
		buffer.append(" rolesToPlayerInfos: ");
		buffer.append(rolesToPlayerInfos);
		buffer.append(" number of states: ");
		buffer.append(getXmlStates().size());
		buffer.append(" jointMovesStrings: ");
		buffer.append(getJointMovesStrings());
		buffer.append(" errorMessages: ");
		buffer.append(getErrorMessages());
		buffer.append("]");
		return buffer.toString();
	}
}