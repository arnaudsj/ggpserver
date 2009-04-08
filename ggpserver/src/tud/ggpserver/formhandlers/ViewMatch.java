package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import javax.naming.NamingException;

import cs227b.teamIago.util.GameState;

import tud.gamecontroller.game.javaprover.Term;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.ggpserver.JavaProverReasonerFactory;
import tud.ggpserver.datamodel.DBConnector;
import tud.ggpserver.datamodel.Match;


public class ViewMatch {
	private DBConnector<Term, GameState> db = new DBConnector<Term, GameState>();
	
	private Match<Term, GameState> match;
	private int stepNumber = 1;

	public int getStepNumber() {
		return stepNumber;
	}

	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}

	public void setMatchID(String matchID) throws NamingException, SQLException {
		match = db.getMatch(matchID, new JavaProverReasonerFactory());
	}

	public Match getMatch() {
		return match;
	}
	
	public List<String> getMoves() {
		if ((stepNumber < 1) || stepNumber > (match.getNumberOfStates() - 1)) {  // -1, because there is one less jointmove than states
			return new LinkedList<String>();
		}
		return match.getJointMovesStrings().get(stepNumber - 1);
	}
	
	public List<GameControllerErrorMessage> getErrorMessages() {
		if ((stepNumber < 1) || (stepNumber > match.getNumberOfStates())) {
			return new LinkedList<GameControllerErrorMessage>();
		}
		return match.getErrorMessages().get(stepNumber - 1);
	}
}
