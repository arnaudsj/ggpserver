package tud.ggpserver.formhandlers;

import java.sql.SQLException;

import javax.naming.NamingException;

import tud.gamecontroller.game.javaprover.Term;
import tud.ggpserver.JavaProverReasonerFactory;
import tud.ggpserver.datamodel.DBConnector;
import tud.ggpserver.datamodel.Game;
import tud.ggpserver.util.KIFSyntaxFormatter;
import cs227b.teamIago.util.GameState;

public class ViewGame {
	protected final DBConnector<Term, GameState> db = new DBConnector<Term, GameState>();

	private String name = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Game<Term, GameState> getGame() throws NamingException, SQLException {
		return db.getGame(name, JavaProverReasonerFactory.getInstance());
	}
	
	public String getGameDescription() throws NamingException, SQLException {
		return new KIFSyntaxFormatter(getGame().getKIFGameDescription()).getFormattedGameDescription();
	}
}
