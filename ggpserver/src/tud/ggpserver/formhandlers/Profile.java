package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.RemotePlayerInfo;
import tud.ggpserver.datamodel.User;

public class Profile {
	private final static AbstractDBConnector db = DBConnectorFactory.getDBConnector();
	
	private User user = null;
	private List<RemotePlayerInfo> players = null;

	public String getUserName() {
		if (user == null) {
			throw new IllegalStateException("user not set!");
		}
		return user.getUserName();
	}

	public void setUserName(String userName) throws NamingException, SQLException {
		user = db.getUser(userName);
	}

	@SuppressWarnings("unchecked")
	public List<RemotePlayerInfo> getPlayers() throws NamingException, SQLException {
		assert (user != null);
		
		if (players == null) {
			players = db.getPlayerInfosForUser(user.getUserName());
		}
		
		return players;
	}
	

	
}
