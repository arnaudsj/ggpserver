package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;

public class ViewState {
	private String matchID;
	private int stepNumber = -1;
	
	private final static AbstractDBConnector db = DBConnectorFactory.getDBConnector();


	public void setMatchID(String matchID) {
		this.matchID = matchID;
	}

	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}

	@SuppressWarnings("unchecked")
	public String getXmlState() throws SQLException, NamingException {
		List<String> states = db.getMatch(matchID).getXmlStates();
		
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
