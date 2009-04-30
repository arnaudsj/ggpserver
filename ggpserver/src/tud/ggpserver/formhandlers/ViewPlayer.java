package tud.ggpserver.formhandlers;

import java.sql.SQLException;

import javax.naming.NamingException;

import tud.gamecontroller.players.LocalPlayerInfo;
import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.RemotePlayerInfo;
import tud.ggpserver.datamodel.User;

public class ViewPlayer {
	private final static AbstractDBConnector db = DBConnectorFactory.getDBConnector();

	private PlayerInfo playerInfo = null;
	
	public void setName(String name) throws NamingException, SQLException {
		playerInfo = db.getPlayerInfo(name);
	}
	
	public PlayerInfo getPlayer() {
		return playerInfo;
	}
	
	public User getOwner() throws SQLException {
		if(playerInfo instanceof RemotePlayerInfo){
			return ((RemotePlayerInfo)playerInfo).getOwner();
		}else if(playerInfo instanceof LocalPlayerInfo){
			return db.getUser("admin");
		}
		return null;
	}
	
	public String getStatus() {
		if(playerInfo instanceof RemotePlayerInfo){
			return ((RemotePlayerInfo)playerInfo).getStatus();
		}else if(playerInfo instanceof LocalPlayerInfo){
			return "active";
		}
		return "?";
	}

	public String getName() {
		return playerInfo.getName();
	}
}
