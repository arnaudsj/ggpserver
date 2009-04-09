package tud.ggpserver.formhandlers;

import java.sql.SQLException;

import javax.naming.NamingException;

import tud.gamecontroller.game.javaprover.Term;
import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.DBConnector;
import cs227b.teamIago.util.GameState;

public class ViewPlayer {
	protected final DBConnector<Term, GameState> db = new DBConnector<Term, GameState>();

	private PlayerInfo playerInfo = null;
	
	public void setName(String name) throws NamingException, SQLException {
		playerInfo = db.getPlayerInfo(name);
	}
	
	public PlayerInfo getPlayer() throws NamingException, SQLException {
		return playerInfo;
	}
}
