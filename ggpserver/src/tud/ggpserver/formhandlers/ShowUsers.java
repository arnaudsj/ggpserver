package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.User;


public class ShowUsers extends AbstractPager {
	protected AbstractDBConnector<?, ?> db = DBConnectorFactory.getDBConnector();

	public List<User> getUsers() throws NamingException, SQLException {
		return db.getUsers(startRow, numDisplayedRows);
	}
	
	@Override
	protected String getTableName() {
		return "users";
	}

	@Override
	public String getTargetJsp() {
		return "show_users.jsp";
	}

}
