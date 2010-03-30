package tud.ggpserver.formhandlers.inc;

import static tud.ggpserver.datamodel.DBConnectorFactory.getDBConnector;

import java.sql.SQLException;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.User;
import tud.ggpserver.scheduler.MatchRunner;

public class Availability {
	
	protected final AbstractDBConnector<?, ?> db = DBConnectorFactory.getDBConnector();
	private User user = null;
	private String matchID;
	
	public void setUserName(String userName) throws SQLException {
		user = getDBConnector().getUser(userName);
	}
	
	public void setMatchID (String matchID) {
		this.matchID = matchID;
	}
	
	public void setAvailable (int i) {
		if (i != 1) return;
		// lets us be available for matchID!
		MatchRunner.getInstance().setAccepted(matchID, user.getUserName());
	}
	
}
