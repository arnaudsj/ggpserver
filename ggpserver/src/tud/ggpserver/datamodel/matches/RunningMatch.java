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

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import tud.gamecontroller.GameControllerListener;
import tud.gamecontroller.XMLGameStateWriter;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.impl.State;
import tud.gamecontroller.logging.ErrorMessageListener;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.players.PlayerFactory;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.scrambling.GameScramblerInterface;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DuplicateInstanceException;
import tud.ggpserver.datamodel.User;
import tud.ggpserver.datamodel.dblists.DynamicDBBackedList;
import tud.ggpserver.datamodel.dblists.ErrorMessageAccessor;
import tud.ggpserver.datamodel.dblists.JointMovesAccessor;
import tud.ggpserver.datamodel.dblists.XMLStateAccessor;


public class RunningMatch<TermType extends TermInterface, ReasonerStateInfoType>
		extends ServerMatch<TermType, ReasonerStateInfoType>
		implements
		RunnableMatchInterface<TermType, State<TermType, ReasonerStateInfoType>>,
		GameControllerListener, ErrorMessageListener<TermType, ReasonerStateInfoType> {

	private static final Logger logger = Logger.getLogger(RunningMatch.class.getName());

	private final MoveFactoryInterface<? extends MoveInterface<TermType>> moveFactory;
	private GameScramblerInterface gameScrambler;

	/**
	 * JointMove 0 = joint move leading from initial state to State 1.
	 * This list always has one less element than the states list.
	 * 
	 * jointMoves will only be non-empty if they were calculated by 
	 * running this match. If this match comes from the database 
	 * instead, jointMoves will be empty.
	 * 
	 * It's possible to have several RunningMatch instances representing 
	 * the same match: We can't assume that the first instance is cached.
	 * Only one of them will actually be running the match, but the others
	 * can be used for display.   
	 */
	private List<JointMoveInterface<? extends TermInterface>> jointMoves = new LinkedList<JointMoveInterface<? extends TermInterface>>();   // all joint moves executed so far

	private Map<RoleInterface<TermType>, Player<TermType>> players;
	private List<Player<TermType>> orderedPlayers = null;

	/**
	 * stepNumber is always the same as errorMessages.size().
	 * 
	 *  before the game starts: stepNumber == 0
	 *  after START:            stepNumber == 1
	 *  after first PLAY:       stepNumber == 2
	 *  ...
	 *  after n^th PLAY:        stepNumber == n + 1
	 *  after STOP:             stepNumber == n + 1 (intentionally!)
	 *  
	 */
	private int stepNumber = 0;
	

	public RunningMatch(
			String matchID,
			GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game,
			int startclock,
			int playclock,
			Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> rolesToPlayerInfos,
			Date startTime,
			boolean scrambled,
			String tournamentID,
			double weight,
			User owner, AbstractDBConnector<TermType, ReasonerStateInfoType> db,
			MoveFactoryInterface<? extends MoveInterface<TermType>> movefactory,
			GameScramblerInterface gameScrambler) {
		super(matchID, game, startclock, playclock, rolesToPlayerInfos, startTime, scrambled, tournamentID, weight, owner, db);
		this.moveFactory = movefactory;
		this.gameScrambler = gameScrambler;
	}
	
	/**
	 * Sets this matches status to "aborted", and returns the new aborted match.
	 * This object must not be used any more after calling this method.
	 */
	public AbortedMatch<TermType, ReasonerStateInfoType> toAborted() throws SQLException {
		getDB().setMatchStatus(getMatchID(), ServerMatch.STATUS_ABORTED);
		return getDB().getAbortedMatch(getMatchID());
	}
	
	/**
	 * Sets this matches status to "finished", and returns the new finished match.
	 * This object must not be used any more after calling this method.
	 */
	public FinishedMatch<TermType, ReasonerStateInfoType> toFinished() throws SQLException {
		getDB().setMatchStatus(getMatchID(), ServerMatch.STATUS_FINISHED);
		return getDB().getFinishedMatch(getMatchID());
	}

	@Override
	public String getStatus() {
		return ServerMatch.STATUS_RUNNING;
	}
	
	@Override
	public List<List<String>> getJointMovesStrings() {
		{
			if (jointMovesStrings == null) {
				jointMovesStrings = new DynamicDBBackedList<List<String>>(new JointMovesAccessor(getMatchID(), getDB()), true); 
			}
			return jointMovesStrings;
		}
	}

	@Override
	public List<String> getXmlStates() {
		if (xmlStates == null) {
			xmlStates = new DynamicDBBackedList<String>(new XMLStateAccessor(getMatchID(), getDB(), getGame().getStylesheet()), false);
		}
		return xmlStates;
	}

	@Override
	public List<List<GameControllerErrorMessage>> getErrorMessages() {
		// error messages shouldn't be cached for a running match, because
		// sometimes there is one more state than error messages, and the db
		// can't know if there was no error, or if it hasn't been written
		// yet, so a wrong (empty) result might get cached for the currently
		// running state.
		return new DynamicDBBackedList<List<GameControllerErrorMessage>>(new ErrorMessageAccessor(getMatchID(), getDB()), true);
	}

	@Override
	public List<? extends Player<TermType>> getOrderedPlayers() {
		if (players == null) {
			initPlayers();
		}
		if (orderedPlayers == null) {
			orderedPlayers = new LinkedList<Player<TermType>>();
			for (RoleInterface<TermType> role : getGame().getOrderedRoles()) {
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
	
	private void initPlayers() {
		players = new HashMap<RoleInterface<TermType>, Player<TermType>>();
		
		for (RoleInterface<TermType> role : getGame().getOrderedRoles()) {
			PlayerInfo playerInfo = getRolesToPlayerInfos().get(role);
			
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
	@Override
	public void gameStarted(RunnableMatchInterface<? extends TermInterface, ?> match, StateInterface<? extends TermInterface, ?> currentState) {
		stepNumber++;
		
		// only store the non-terminal states, because the terminal state will be stored in gameStopped()
		if (!currentState.isTerminal()) {
			// usually, the initial state won't be terminal, but of course it's possible to write such a stupid game
			updateXmlState(currentState, null);
		}
	}

	@Override
	public void gameStep(JointMoveInterface<? extends TermInterface> jointMove, StateInterface<? extends TermInterface, ?> currentState) {
		updateJointMove(jointMove); // this has to be done BEFORE storing the state (because updateXmlState reads jointMoves)

		stepNumber++;
		
		if (!currentState.isTerminal()) {
			// only store the non-terminal states, because the terminal state will be stored in gameStopped()
			updateXmlState(currentState, null);
		}		
	}

	@Override
	public void gameStopped(StateInterface<? extends TermInterface, ?> currentState, Map<? extends RoleInterface<?>, Integer> goalValues) {
		assert(currentState.isTerminal());
		
		updateGoalValues(goalValues);
		updateXmlState(currentState, goalValues);
		
		// Clear the jointMoves list, they are only needed for running matches.
		jointMoves = new LinkedList<JointMoveInterface<? extends TermInterface>>();
	}
	
	/* (non-Javadoc)
	 * @see tud.ggpserver.datamodel.matches.ErrorMessageListener#addErrorMessage(tud.gamecontroller.logging.GameControllerErrorMessage)
	 */
	@Override
	public void notifyErrorMessage(GameControllerErrorMessage errorMessage) {
		try {
			getDB().addErrorMessage(getMatchID(), stepNumber, errorMessage);
		} catch (SQLException e) {
			logger.severe("GameControllerErrorMessage - exception: " + e); //$NON-NLS-1$
		}
	}

	private void updateGoalValues(Map<? extends RoleInterface<?>, Integer> goalValues) {
		try {
			getDB().setMatchGoalValues(this, goalValues);
		} catch (SQLException e) {
			logger.severe("Map<? extends RoleInterface<?>,Integer> - exception: " + e); //$NON-NLS-1$
		}
	}

	private void updateJointMove(JointMoveInterface<? extends TermInterface> jointMove) {
//		// update jointMovesStrings 
//		List<String> jointMoveString = new LinkedList<String>();
//		for (MoveInterface<? extends TermInterface> move : jointMove.getOrderedMoves()) {
//			jointMoveString.add(move.getKIFForm());
//		}
//		jointMovesStrings.add(jointMoveString);

		// update jointMoves
		jointMoves.add(jointMove);
		

		assert(stepNumber == jointMoves.size());
		
		try {
			getDB().addJointMove(getMatchID(), stepNumber, jointMove);
		} catch (SQLException e) {
			logger.severe("JointMoveInterface<? extends TermInterface> - exception: " + e); //$NON-NLS-1$
		} catch (DuplicateInstanceException e) {
			logger.severe("JointMoveInterface<? extends TermInterface> - exception: " + e); //$NON-NLS-1$
		}
	}

	private void updateXmlState(StateInterface<? extends TermInterface, ?> currentState, Map<? extends RoleInterface<?>, Integer> goalValues) {
		String xmlState = XMLGameStateWriter.createXMLOutputStream(this, currentState, jointMoves, goalValues, getGame().getStylesheet()).toString();
		
//		xmlStates.add(xmlState);
//		int stepNumber = getNumberOfStates();

		assert (stepNumber == getXmlStates().size() + 1);
		
		try {
			getDB().addState(getMatchID(), stepNumber, xmlState);
		} catch (SQLException e) {
			logger.severe("StateInterface<? extends TermInterface,?>, Map<? extends RoleInterface<?>,Integer> - exception: " + e); //$NON-NLS-1$
		} catch (DuplicateInstanceException e) {
			logger.severe("StateInterface<? extends TermInterface,?>, Map<? extends RoleInterface<?>,Integer> - exception: " + e); //$NON-NLS-1$
		}
	}
}
