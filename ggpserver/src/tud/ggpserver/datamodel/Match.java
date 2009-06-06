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

package tud.ggpserver.datamodel;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import tud.gamecontroller.GameControllerListener;
import tud.gamecontroller.XMLGameStateWriter;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.impl.State;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.players.PlayerFactory;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.scrambling.GameScramblerInterface;
import tud.gamecontroller.term.TermInterface;


// XXX: Separate everything possible into a new class RunnableMatch.
public class Match<TermType extends TermInterface, ReasonerStateInfoType>
		implements
		RunnableMatchInterface<TermType, State<TermType, ReasonerStateInfoType>>,
		GameControllerListener {

	private static final Logger logger = Logger.getLogger(Match.class.getName());

	public static final String STATUS_NEW = "new";
	public static final String STATUS_RUNNING = "running";
	public static final String STATUS_FINISHED = "finished";
	public static final String STATUS_ABORTED = "aborted";
	
	private final Date startTime;
	private final Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> rolesToPlayers;
	private List<PlayerInfo> orderedPlayerInfos = null;
	private final MoveFactoryInterface<? extends MoveInterface<TermType>> moveFactory;
	private final GameScramblerInterface gameScrambler;

	private String status = STATUS_NEW;
	private Map<? extends RoleInterface<?>, Integer> goalValues;
	private List<Integer> orderedGoalValues;
	
	/**
	 * State 0 = initial state, State 1 = state after first joint move, ..., State n = final state
	 * 
	 * This list will only be non-empty for running matches.
	 */
	private final List<String> xmlStates;
	
	/**
	 * JointMove 0 = joint move leading from initial state to State 1.
	 * This list always has one less element than the states list.
	 * 
	 * jointMoves will only be non-empty if they were calculated by 
	 * running this match. If this match comes from the database 
	 * instead, jointMoves will be empty and jointMovesStrings will
	 * be filled.  
	 */
	private List<JointMoveInterface<? extends TermInterface>> jointMoves = new LinkedList<JointMoveInterface<? extends TermInterface>>();   // all joint moves executed so far
	private List<List<String>> jointMovesStrings = new LinkedList<List<String>>();   // this is an ugly hack because it's so hard to generate the correct sort of "real" joint moves from inside the AbstractDBConnector
	
	/**
	 * - errors from the start message and first play message go to index 0
	 * - errors from the second play message go to index 1
	 * - ...
	 * - errors from the last (n^th) play message go to index n-1
	 * - errors from the stop message go to index n
	 * The match has n+1 states, n play messages and the stop message, therefore there is an entry in errorMessages for each state.
	 */
	private List<List<GameControllerErrorMessage>> errorMessages = new LinkedList<List<GameControllerErrorMessage>>();
	
	private final AbstractDBConnector<TermType, ReasonerStateInfoType> db;

	private Tournament<TermType, ReasonerStateInfoType> tournament;

	private final String matchID;
	private final GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game;
	private final int startclock;
	private final int playclock;
	
	private Map<RoleInterface<TermType>, Player<TermType>> players;
	private List<Player<TermType>> orderedPlayers = null;
	
	
	/**
	 * Use DBConnectorFactory.getDBConnector().getMatch() instead 
	 */
	protected Match(
			String matchID,
			GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game,
			int startclock,
			int playclock,
			Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> rolesToPlayers,
			Date startTime,
			MoveFactoryInterface<? extends MoveInterface<TermType>> movefactory,
			GameScramblerInterface gamescrambler,
			Tournament<TermType, ReasonerStateInfoType> tournament,
			AbstractDBConnector<TermType, ReasonerStateInfoType> db) {
		this.matchID = matchID;
		this.game = game;
		this.startclock = startclock;
		this.playclock = playclock;
		this.rolesToPlayers = rolesToPlayers;
		this.startTime = startTime;
		this.moveFactory = movefactory;
		this.gameScrambler = gamescrambler;
		this.tournament = tournament;
		this.xmlStates = new XMLStatesList(matchID, db);
		this.db = db;
	}

	@Override
	public List<? extends Player<TermType>> getOrderedPlayers() {
		if (players == null) {
			initPlayers();
		}
		if (orderedPlayers == null) {
			orderedPlayers = new LinkedList<Player<TermType>>();
			for (RoleInterface<TermType> role : game.getOrderedRoles()) {
				orderedPlayers.add(players.get(role));
			}
		}
		return orderedPlayers;
	}

	@Override
	public Player<TermType> getPlayer(RoleInterface<TermType> role) {
		if (players == null) {
			initPlayers();
		}
		return players.get(role);
	}

	@Override
	public Collection<? extends Player<TermType>> getPlayers() {
		if (players == null) {
			initPlayers();
		}
		return players.values();
	}
	
	public List<? extends PlayerInfo> getOrderedPlayerInfos() {
		if (orderedPlayerInfos  == null) {
			orderedPlayerInfos = new LinkedList<PlayerInfo>();
			for (RoleInterface<TermType> role : getGame().getOrderedRoles()) {
				orderedPlayerInfos.add(rolesToPlayers.get(role));
			}
		}
		return orderedPlayerInfos;	}

	public PlayerInfo getPlayerInfo(RoleInterface<TermType> role) {
		return rolesToPlayers.get(role);
	}

	public Collection<? extends PlayerInfo> getPlayerInfos() {
		return rolesToPlayers.values();
	}

	public Date getStartTime() {
		return new Date(startTime.getTime());
	}

	private void initPlayers() {
		players = new HashMap<RoleInterface<TermType>, Player<TermType>>();
		
		for (RoleInterface<TermType> role : game.getOrderedRoles()) {
			PlayerInfo playerInfo = rolesToPlayers.get(role);
			
			Player<TermType> player = PlayerFactory.<TermType> createPlayer(playerInfo, moveFactory, gameScrambler);
			players.put(role, player);
		}
	}

	/**
	 * The actual order of calls and START / PLAY / STOP messages is like this (if the initial state is not terminal):<br>
	 * 
	 * gameStarted() --- adds errorMessages(0), xmlStates(0)<br>
	 * START         --- all errors go to errorMessages(0)<br>
	 * PLAY          --- all errors go to errorMessages(0)<br>
	 * gameStep()    --- adds errorMessages(1), xmlStates(1), jointMoves(0), jointMovesStrings(0)<br>
	 * PLAY          --- all errors go to errorMessages(1)<br>
	 * gameStep()...
	 * ...<br>
	 * PLAY          --- all errors go to errorMessages(n-1)<br>
	 * gameStep()    --- adds errorMessages(n), jointMoves(n-1), jointMovesStrings(n-1), currentState is terminal -> state is not added <br>
	 * gameStopped() --- adds xmlStates(n)<br>
	 * STOP          --- all errors go to errorMessages(n)<br>
	 * 
	 * So finally, errorMessages and xmlStates will have size n+1, while jointMoves and jointMovesStrings will have size (n).
	 * 
	 * ===============
	 * 
	 * If the initial state is terminal, the order will be like this:
	 * 
	 * gameStarted() --- adds errorMessages(0), DOES NOT ADD xmlStates(0)<br>
	 * START         --- all errors go to errorMessages(0)<br>
	 * gameStopped() --- adds xmlStates(0)<br>
	 * STOP          --- all errors go to errorMessages(0)<br>
	 * 
	 * Like above, errorMessages and xmlStates will have size n+1 [== 1], while jointMoves and jointMovesStrings will have size (n) [== 0].
	 */
	public void gameStarted(RunnableMatchInterface<? extends TermInterface, ?> match, StateInterface<? extends TermInterface, ?> currentState) {
		updateStatus(STATUS_RUNNING);
		
		// prepare the error messages list for new entries  
		errorMessages.add(new LinkedList<GameControllerErrorMessage>());
		
		// only store the non-terminal states, because the terminal state will be stored in gameStopped()
		if (!currentState.isTerminal()) {
			// usually, the initial state won't be terminal, but of course it's possible to write such a stupid game
			updateXmlState(currentState, null);
		}
	}

	public void gameStep(JointMoveInterface<? extends TermInterface> jointMove, StateInterface<? extends TermInterface, ?> currentState) {
		updateJointMove(jointMove); // this has to be done BEFORE storing the state (because updateXmlState reads jointMoves)

		// prepare the error messages list for new entries  
		errorMessages.add(new LinkedList<GameControllerErrorMessage>());
		
		if (!currentState.isTerminal()) {
			// only store the non-terminal states, because the terminal state will be stored in gameStopped()
			updateXmlState(currentState, null);
		}		
	}

	public void gameStopped(StateInterface<? extends TermInterface, ?> currentState, Map<? extends RoleInterface<?>, Integer> goalValues) {
		assert(currentState.isTerminal());
		
		updateGoalValues(goalValues);
		updateXmlState(currentState, goalValues);
		updateStatus(STATUS_FINISHED);
		
		// Clear the jointMoves list, they are only needed for running matches.
		jointMoves = new LinkedList<JointMoveInterface<? extends TermInterface>>();
	}
	
	public String getStatus() {
		return status;
	}

	public void updateStatus(String status) {
		try {
			db.setMatchStatus(this, status);
			setStatus(status);
		} catch (SQLException e) {
			logger.severe("String - exception: " + e); //$NON-NLS-1$
		}
	}

	public Map<? extends RoleInterface<?>, Integer> getGoalValues() {
		return goalValues;
	}

	protected void updateGoalValues(Map<? extends RoleInterface<?>, Integer> goalValues) {
		try {
			db.setMatchGoalValues(this, goalValues);
			setGoalValues(goalValues);
		} catch (SQLException e) {
			logger.severe("Map<? extends RoleInterface<?>,Integer> - exception: " + e); //$NON-NLS-1$
		}
	}

	public List<Integer> getOrderedGoalValues() {
		if (goalValues == null) {
			return null;
		}
		if (orderedGoalValues == null) {
			orderedGoalValues = new LinkedList<Integer>();
			for (RoleInterface<?> role : getGame().getOrderedRoles()) {
				orderedGoalValues.add(goalValues.get(role));
			}
		}
		return orderedGoalValues;
	}

	public void setGoalValues(Map<? extends RoleInterface<?>, Integer> goalValues) {
		this.goalValues = goalValues;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public List<List<GameControllerErrorMessage>> getErrorMessages() {
		return errorMessages;
	}

	public void setErrorMessages(
			List<List<GameControllerErrorMessage>> errorMessages) {
		this.errorMessages = errorMessages;
	}
	
	public void updateErrorMessage(GameControllerErrorMessage errorMessage) {
		int stepNumber = errorMessages.size();
		errorMessages.get(stepNumber - 1).add(errorMessage);
		
		try {
			db.addErrorMessage(getMatchID(), stepNumber, errorMessage);
		} catch (SQLException e) {
			logger.severe("GameControllerErrorMessage - exception: " + e); //$NON-NLS-1$
		}
	}

	protected void updateJointMove(
			JointMoveInterface<? extends TermInterface> jointMove) {
		// update jointMovesStrings 
		List<String> jointMoveString = new LinkedList<String>();
		for (MoveInterface<? extends TermInterface> move : jointMove.getOrderedMoves()) {
			jointMoveString.add(move.getKIFForm());
		}
		jointMovesStrings.add(jointMoveString);

		// update jointMoves
		jointMoves.add(jointMove);
		
		int stepNumber = jointMoves.size();
		
		try {
			db.addJointMove(getMatchID(), stepNumber, jointMove);
		} catch (SQLException e) {
			logger.severe("JointMoveInterface<? extends TermInterface> - exception: " + e); //$NON-NLS-1$
		} catch (DuplicateInstanceException e) {
			logger.severe("JointMoveInterface<? extends TermInterface> - exception: " + e); //$NON-NLS-1$
		}
	}

	public String getXmlState(int stepNumber) {
		if (stepNumber < 1 || stepNumber > getNumberOfStates()) {
			throw new IndexOutOfBoundsException();
		}

		String stylesheet = getGame().getStylesheet();
		
		// this is a hack to show old matches with the right stylesheets (e.g., if the stylesheet for a game was changed after the match)
		// we just replace the stylesheet information with the current one  
		Pattern styleSheetPattern = Pattern.compile("<\\?xml-stylesheet type=\"text/xsl\" href=\"[^\"]*\"\\?>");
		String styleSheetReplacement = "<?xml-stylesheet type=\"text/xsl\" href=\""+stylesheet+"\"?>";
		
		// TODO: This hack should be moved to AbstractDBConnector.getXmlStates() 
		
		return styleSheetPattern.matcher(getXmlStates().get(stepNumber - 1)).replaceFirst(styleSheetReplacement);
	}
	
	private List<String> getXmlStates() {
		return xmlStates;
	}
	
	protected void updateXmlState(StateInterface<? extends TermInterface, ?> currentState, Map<? extends RoleInterface<?>, Integer> goalValues) {
		String xmlState = XMLGameStateWriter.createXMLOutputStream(this, currentState, jointMoves, goalValues, getGame().getStylesheet()).toString();
		
//		xmlStates.add(xmlState);
//		int stepNumber = getNumberOfStates();

		int stepNumber = getNumberOfStates() + 1;
		
		try {
			db.addState(getMatchID(), stepNumber, xmlState);
		} catch (SQLException e) {
			logger.severe("StateInterface<? extends TermInterface,?>, Map<? extends RoleInterface<?>,Integer> - exception: " + e); //$NON-NLS-1$
		} catch (DuplicateInstanceException e) {
			logger.severe("StateInterface<? extends TermInterface,?>, Map<? extends RoleInterface<?>,Integer> - exception: " + e); //$NON-NLS-1$
		}
	}

	public List<List<String>> getJointMovesStrings() {
		return jointMovesStrings;
	}

	public void setJointMovesStrings(List<List<String>> jointMovesStrings) {
		this.jointMovesStrings = jointMovesStrings;
	}
	
	public int getNumberOfStates() {
		return getXmlStates().size();
	}
	
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

	/**
	 * @generated by CodeSugar http://sourceforge.net/projects/codesugar */
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[Match:");
		buffer.append(" matchID: ");
		buffer.append(getMatchID());
		buffer.append(" game: ");
		buffer.append(getGame());
		buffer.append(" startclock: ");
		buffer.append(getStartclock());
		buffer.append(" playclock: ");
		buffer.append(getPlayclock());
		buffer.append(" players: ");
		buffer.append(getPlayers());
		buffer.append(" orderedPlayers: ");
		buffer.append(getOrderedPlayers());
		buffer.append(" startTime: ");
		buffer.append(startTime);
		buffer.append(" rolesToPlayers: ");
		buffer.append(rolesToPlayers);
		buffer.append(" orderedPlayerInfos: ");
		buffer.append(getOrderedPlayerInfos());
		buffer.append(" moveFactory: ");
		buffer.append(moveFactory);
		buffer.append(" gameScrambler: ");
		buffer.append(gameScrambler);
		buffer.append(" status: ");
		buffer.append(status);
		buffer.append(" goalValues: ");
		buffer.append(goalValues);
		buffer.append(" orderedGoalValues: ");
		buffer.append(getOrderedGoalValues());
//		buffer.append(" xmlStates: ");
//		buffer.append(xmlStates);
		buffer.append(" numberOfStates: ");
		buffer.append(getNumberOfStates());
		buffer.append(" jointMoves: ");
		buffer.append(jointMoves);
		buffer.append(" jointMovesStrings: ");
		buffer.append(jointMovesStrings);
		buffer.append(" errorMessages: ");
		buffer.append(errorMessages);
		buffer.append(" db: ");
		buffer.append(db);
		buffer.append("]");
		return buffer.toString();
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

	public Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> getRolesToPlayers() {
		return new HashMap<RoleInterface<TermType>, PlayerInfo>(rolesToPlayers);
	}

	public Tournament<TermType, ReasonerStateInfoType> getTournament() {
		return tournament;
	}

	public GameInterface<TermType, State<TermType, ReasonerStateInfoType>> getGame() {
		return game;
	}

	public String getMatchID() {
		return matchID;
	}

	public int getPlayclock() {
		return playclock;
	}

	public int getStartclock() {
		return startclock;
	}
	

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((matchID == null) ? 0 : matchID.hashCode());
		return result;
	}

	/**
	 * Two matches are considered equal iff their matchID is equal (i.e., matchID is a unique identifier).
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Match other = (Match) obj;
		if (matchID == null) {
			if (other.matchID != null)
				return false;
		} else if (!matchID.equals(other.matchID))
			return false;
		return true;
	}
	
	public RunnableMatchInterface<TermType, StateInterface<TermType,? extends State<TermType, ReasonerStateInfoType>>> makeRunnable() {
		// XXX
		return null;
	}
}
