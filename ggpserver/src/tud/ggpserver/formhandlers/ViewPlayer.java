package tud.ggpserver.formhandlers;

import java.sql.SQLException;

import javax.naming.NamingException;

import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;

public class ViewPlayer {
	private final static AbstractDBConnector db = DBConnectorFactory.getDBConnector();

	private PlayerInfo playerInfo = null;
	
	public void setName(String name) throws NamingException, SQLException {
		playerInfo = db.getPlayerInfo(name);
	}
	
	public PlayerInfo getPlayer() throws NamingException, SQLException {
		return playerInfo;
	}
}
