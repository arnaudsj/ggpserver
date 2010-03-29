package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import tud.gamecontroller.auxiliary.Pair;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.players.HumanPlayer;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.User;
import tud.ggpserver.datamodel.matches.RunningMatch;
import tud.ggpserver.scheduler.MatchRunner;
import tud.ggpserver.util.StateXMLExporter;

public class Play {
	
	protected static final Logger logger = Logger.getLogger(Play.class.getName());
	
	protected final AbstractDBConnector<?, ?> db = DBConnectorFactory.getDBConnector();
	private User user;
	private String role;
	
	private String matchID;
	private RunningMatch<?,?> match;
	private HumanPlayer<?,?> player;
	private int stepNumber;
	
	private MoveInterface<?> move;
	private int forStepNumber;
	
	
	public void setMatchID (String matchID) throws SQLException {
		// set our match
		this.matchID = matchID;
		match = MatchRunner.getInstance().getRunningMatch(matchID);
		// set stepNumber
		if (match != null)
			stepNumber = match.getStringStates().size(); // return the last known state of the match
	}
	
	public void setUserName (String userName) throws SQLException {
		user = db.getUser(userName);
	}
	
	@SuppressWarnings("unchecked")
	public void setRole (String role) {
		this.role = role;
		// find our human player
		int i = 0;
		for (RoleInterface<?> r: match.getOrderedPlayerRoles()) {
			if (r.getPrefixForm().toUpperCase().equals(role.toUpperCase())) break;
			i++;
		}
		logger.info("role "+i+": "+match.getOrderedPlayerNames().get(i)+", "+user.getUserName());
		if (match.getOrderedPlayerNames().get(i).toUpperCase().equals(user.getUserName().toUpperCase()))  // only allow the correct logged-in user to access and play for this role
			player = (HumanPlayer) match.getOrderedPlayers().get(i);
	}
	
	public boolean isPlaying() throws SQLException {
		if (match == null) return false;
		if (player == null) return false;
		return true;
	}
	
	public boolean isScheduled() {
		if (MatchRunner.getInstance().getScheduledMatch(matchID) != null)
			return true;
		return false;
	}
	
	public String getMatchID () {
		return matchID;
	}
	
	public String getRole () {
		return this.role;
	}
	
	public List<String> getLegalMoves () {
		
		List<? extends MoveInterface<?>> moves = player.getLegalMoves();
		logger.info("moves = "+moves);
		if (moves == null) {
			// TODO: act correspondingly if this returns false
			return null;
		}
		
		List<String> stringMoves = new LinkedList<String>();
		for (MoveInterface<?> move: moves)
			stringMoves.add(move.getPrefixForm());
		
		return stringMoves;
		
	}
	
	@SuppressWarnings("unchecked")
	public void setChosenMove (int i) throws SQLException {
		
		if (!isPlaying()) // very important! otherwise, one could just send moves for a non-owned role, and they would be accepted
			return;
		
		if (i == -2) {
			this.player.setReady();
		} else if (i == -1) {
			this.player.confirm(forStepNumber);
		} else {
			try {
				move = player.getLegalMoves().get(i);
			} catch (IndexOutOfBoundsException ex) {
				move = null;
			}
			//logger.info("Play, setChosenMove("+move+")");
			if (move != null)
				this.player.setMove( (MoveInterface)move, forStepNumber);
		}
	}
	
	public void setForStepNumber(int forStepNumber) {
		this.forStepNumber = forStepNumber;
	}
	
	private boolean isReady() {
		return this.player.isReady();
	}

	public String getMove () {
		if (move == null) {
			MoveInterface<?> m = this.player.getMove();
			if (m != null) {
				return m.getPrefixForm();
			} else {
				return null;
			}
		}
		return this.move.getPrefixForm();
	}
	
	private boolean getConfirmed() {
		return this.player.hasConfirmed(stepNumber);
	}
	
	public int getStepNumber () {
		return stepNumber;
	}
	
	public String getXmlState() {
		List<Pair<Date,String>> stringStates = match.getStringStates();
		return StateXMLExporter.getStepXML(match, stringStates, stepNumber, getRole(), true, isReady(), getLegalMoves(), getMove(), getConfirmed());
	}
	
}
