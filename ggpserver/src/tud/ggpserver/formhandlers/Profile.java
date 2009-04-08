package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import tud.gamecontroller.game.javaprover.Term;
import tud.ggpserver.datamodel.DBConnector;
import tud.ggpserver.datamodel.RemotePlayerInfo;
import tud.ggpserver.datamodel.User;
import cs227b.teamIago.util.GameState;

public class Profile {
	private DBConnector<Term, GameState> db = new DBConnector<Term, GameState>();
	
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

	public List<RemotePlayerInfo> getPlayers() throws NamingException, SQLException {
		assert (user != null);
		
		if (players == null) {
			players = db.getPlayerInfosForUser(user.getUserName());
		}
		
		return players;
	}
	

	
}
