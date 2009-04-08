package tud.ggpserver.formhandlers;

import java.sql.SQLException;

import javax.naming.NamingException;

import tud.gamecontroller.game.javaprover.Term;
import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.DBConnector;
import cs227b.teamIago.util.GameState;

public class ViewPlayer {
	protected final DBConnector<Term, GameState> db = new DBConnector<Term, GameState>();

	private String name = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public PlayerInfo getPlayer() throws NamingException, SQLException {
		return db.getPlayerInfo(name);
	}

}
