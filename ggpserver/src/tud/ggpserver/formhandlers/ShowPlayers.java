package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;

public class ShowPlayers extends AbstractPager {
	protected final static AbstractDBConnector<?, ?> db = DBConnectorFactory.getDBConnector();

	public List<PlayerInfo> getPlayers() throws NamingException, SQLException {
		return db.getPlayerInfos(startRow, numDisplayedRows);
	}
	
	@Override
	protected String getTableName() {
		return "players";
	}

	@Override
	public String getTargetJsp() {
		return "show_players.jsp";
	}
}
