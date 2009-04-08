package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import tud.gamecontroller.game.javaprover.Term;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.ggpserver.JavaProverReasonerFactory;
import tud.ggpserver.datamodel.DBConnector;
import cs227b.teamIago.util.GameState;

public class ViewErrors {
	private DBConnector<Term, GameState> db = new DBConnector<Term, GameState>();
	
	private String matchID;
	private int stepNumber;
	
	public List<GameControllerErrorMessage> getErrorMessages() throws NamingException, SQLException {
		return db.getMatch(matchID, JavaProverReasonerFactory.getInstance()).getErrorMessages().get(stepNumber - 1);
	}
	
	public String getMatchID() {
		return matchID;
	}
	public void setMatchID(String matchID) {
		this.matchID = matchID;
	}
	public int getStepNumber() {
		return stepNumber;
	}
	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}
	

}
