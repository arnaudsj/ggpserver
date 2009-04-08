package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import tud.gamecontroller.game.javaprover.Term;
import tud.ggpserver.datamodel.DBConnector;
import cs227b.teamIago.util.GameState;

public class ViewState {
	private String matchID;
	private int stepNumber = -1;
	
	private DBConnector<Term, GameState> db = new DBConnector<Term, GameState>();


	public void setMatchID(String matchID) {
		this.matchID = matchID;
	}

	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}

	public String getXmlState() throws SQLException, NamingException {
		List<String> states = db.getStates(matchID);
		
		int stepNumber;
		if (this.stepNumber < 1 || this.stepNumber > states.size()) {
			// return the last/final state
			stepNumber = states.size(); 
		} else {
			stepNumber = this.stepNumber;
		}
		
		return states.get(stepNumber - 1);
	}
}
