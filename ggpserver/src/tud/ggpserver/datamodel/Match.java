package tud.ggpserver.datamodel;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.naming.NamingException;

import tud.gamecontroller.GameControllerListener;
import tud.gamecontroller.XMLGameStateWriter;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.impl.Game;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.players.PlayerFactory;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.scrambling.GameScramblerInterface;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.scheduler.GameProperties;
import tud.ggpserver.util.HashCodeUtil;

public class Match<TermType extends TermInterface, ReasonerStateInfoType>
		extends tud.gamecontroller.game.impl.Match<TermType, ReasonerStateInfoType> implements GameControllerListener {
	public static final String STATUS_NEW = "new";
	public static final String STATUS_RUNNING = "running";
	public static final String STATUS_FINISHED = "finished";
	public static final String STATUS_ABORTED = "aborted";
	
	private final Date startTime;
	private final Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> playerInfos;
	private List<PlayerInfo> orderedPlayerInfos = null;
	private final MoveFactoryInterface<? extends MoveInterface<TermType>> moveFactory;
	private final GameScramblerInterface gameScrambler;

	private String status = STATUS_NEW;
	private Map<? extends RoleInterface<?>, Integer> goalValues;
	private List<Integer> orderedGoalValues;
	private String stylesheet = null;   // this can remain null (no stylesheet will be used)
	
	/**
	 * State 0 = initial state, State 1 = state after first joint move, ..., State n = final state
	 */
	private List<String> xmlStates = new LinkedList<String>();
	
	/**
	 * JointMove 0 = joint move leading from initial state to State 1.
	 * This list always has one less element than the states list.
	 */
	private List<JointMoveInterface<? extends TermInterface>> jointMoves = new LinkedList<JointMoveInterface<? extends TermInterface>>();   // all joint moves executed so far
	private List<List<String>> jointMovesStrings = new LinkedList<List<String>>();   // this is an ugly hack because it's so hard to generate the correct sort of "real" joint moves from inside the DBConnector
	
	/**
	 * - errors from the start message and first play message go to index 0
	 * - errors from the second play message go to index 1
	 * - ...
	 * - errors from the last (n^th) play message and the stop message go to
	 *   index n
	 * 
	 * ==> the errorMessages list has the same number of elements as the
	 *     jointMoves list, one less than the states list.
	 */
	private List<List<GameControllerErrorMessage>> errorMessages = new LinkedList<List<GameControllerErrorMessage>>();
	
	private DBConnector<TermInterface, ReasonerStateInfoType> db = new DBConnector<TermInterface, ReasonerStateInfoType>();
	
	/**
	 * Use DBConnector.getMatch() instead
	 */
	protected Match(
			String matchID,
			Game<TermType, ReasonerStateInfoType> game,
			int startclock,
			int playclock,
			Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> playerinfos,
			Date startTime,
			MoveFactoryInterface<? extends MoveInterface<TermType>> movefactory,
			GameScramblerInterface gamescrambler) {
		super(matchID, game, startclock, playclock, null);
		this.playerInfos = playerinfos;
		this.startTime = startTime;
		this.moveFactory = movefactory;
		this.gameScrambler = gamescrambler;
		this.stylesheet = GameProperties.getInstance(game.getName()).getStylesheet();
	}

	@Override
	public List<? extends Player<TermType>> getOrderedPlayers() {
		if (players == null) {
			initPlayers();
		}
		return super.getOrderedPlayers();
	}

	@Override
	public Player<TermType> getPlayer(RoleInterface<TermType> role) {
		if (players == null) {
			initPlayers();
		}
		return super.getPlayer(role);
	}

	@Override
	public Collection<? extends Player<TermType>> getPlayers() {
		if (players == null) {
			initPlayers();
		}
		return super.getPlayers();
	}
	
	public List<? extends PlayerInfo> getOrderedPlayerInfos() {
		if (orderedPlayerInfos  == null) {
			orderedPlayerInfos = new LinkedList<PlayerInfo>();
			for (RoleInterface<TermType> role : game.getOrderedRoles()) {
				orderedPlayerInfos.add(playerInfos.get(role));
			}
		}
		return orderedPlayerInfos;	}

	public PlayerInfo getPlayerInfo(RoleInterface<TermType> role) {
		return playerInfos.get(role);
	}

	public Collection<? extends PlayerInfo> getPlayerInfos() {
		return playerInfos.values();
	}

	public Date getStartTime() {
		return startTime;
	}

	/**
	 * Note: after I had written this, I noticed that it does pretty much the
	 * same as AbstractGameControllerRunner.createPlayers(), minus the
	 * filling-up with random players.
	 */
	private void initPlayers() {
		Map<RoleInterface<TermType>, Player<TermType>> myPlayers = new HashMap<RoleInterface<TermType>, Player<TermType>>();
		
		for (RoleInterface<TermType> role : game.getOrderedRoles()) {
			PlayerInfo playerInfo = playerInfos.get(role);
			
			Player<TermType> player = PlayerFactory.<TermType> createPlayer(playerInfo, moveFactory, gameScrambler);
			myPlayers.put(role, player);
		}
		players = myPlayers;
	}

	public void gameStarted(MatchInterface<? extends TermInterface, ?> match, StateInterface<? extends TermInterface, ?> currentState) {
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
		
		if (!currentState.isTerminal()) {
			// prepare the error messages list for new entries  
			errorMessages.add(new LinkedList<GameControllerErrorMessage>());
			
			// only store the non-terminal states, because the terminal state will be stored in gameStopped()
			updateXmlState(currentState, null);
		}
		
	}

	public void gameStopped(StateInterface<? extends TermInterface, ?> currentState, Map<? extends RoleInterface<?>, Integer> goalValues) {
		assert(currentState.isTerminal());
		
		updateGoalValues(goalValues);
		updateXmlState(currentState, goalValues);
		updateStatus(STATUS_FINISHED);
	}
	
	public String getStatus() {
		return status;
	}

	public void updateStatus(String status) {
		try {
			db.setMatchStatus(this, status);
			setStatus(status);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
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
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (DuplicateInstanceException e) {
			e.printStackTrace();
		}
	}
	
	public List<String> getXmlStates() {
		return xmlStates;
	}

	public void setXmlStates(List<String> xmlStates) {
		this.xmlStates = xmlStates;
	}
	
	protected void updateXmlState(StateInterface<? extends TermInterface, ?> currentState, Map<? extends RoleInterface<?>, Integer> goalValues) {
		String xmlState = XMLGameStateWriter.createXMLOutputStream(this, currentState, jointMoves, goalValues, stylesheet).toString();
		xmlStates.add(xmlState);
		
		int stepNumber = xmlStates.size();
		
		try {
			db.addState(getMatchID(), stepNumber, xmlState);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		} catch (DuplicateInstanceException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Match) {
			Match other = (Match) obj;
			return other.getMatchID().equals(getMatchID());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(HashCodeUtil.SEED, getMatchID());
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
		for (List<GameControllerErrorMessage> errorMessages : getErrorMessages()) {
			if (!errorMessages.isEmpty()) {
				return true;
			}
		}
		return false;
	}
}