package tud.ggpserver.formhandlers;

import java.sql.SQLException;

import javax.naming.NamingException;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.User;


public class ViewUser {
	private final static AbstractDBConnector db = DBConnectorFactory.getDBConnector();

	private User user = null;

	public User getUser() {
		return user;
	}

	public void setUserName(String userName) throws NamingException, SQLException {
		this.user = db.getUser(userName);
	}
	
}
