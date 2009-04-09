package tud.ggpserver.formhandlers;

import java.sql.SQLException;

import javax.naming.NamingException;

import cs227b.teamIago.util.GameState;
import tud.gamecontroller.game.javaprover.Term;
import tud.ggpserver.datamodel.DBConnector;
import tud.ggpserver.datamodel.User;


public class ViewUser {
	private DBConnector<Term, GameState> db = new DBConnector<Term, GameState>();

	private User user = null;

	public User getUser() {
		return user;
	}

	public void setUserName(String userName) throws NamingException, SQLException {
		this.user = db.getUser(userName);
	}
	
}
