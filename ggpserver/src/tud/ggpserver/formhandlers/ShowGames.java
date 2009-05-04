package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Game;

public class ShowGames extends AbstractPager {
	protected AbstractDBConnector db = DBConnectorFactory.getDBConnector();

	@SuppressWarnings("unchecked")
	public List<Game> getGames() throws NamingException, SQLException {
		return db.getGames(startRow, numDisplayedRows);
	}

	@Override
	public String getTargetJsp() {
		return "show_games.jsp";
	}

	@Override
	public String getTableName() {
		return "games";
	}
}
