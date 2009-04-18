package tud.ggpserver.formhandlers;

import java.sql.SQLException;

import javax.naming.NamingException;

import tud.gamecontroller.game.impl.Game;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.util.KIFSyntaxFormatter;

public class ViewGame {
	private final static AbstractDBConnector db = DBConnectorFactory.getDBConnector();

	private String name = "";

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Game getGame() throws NamingException, SQLException {
		return db.getGame(name);
	}
	
	public String getGameDescription() throws NamingException, SQLException {
		return new KIFSyntaxFormatter(getGame().getKIFGameDescription()).getFormattedGameDescription();
	}
}
