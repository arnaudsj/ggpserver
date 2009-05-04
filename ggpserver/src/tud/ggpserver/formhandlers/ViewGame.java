package tud.ggpserver.formhandlers;

import java.sql.SQLException;

import javax.naming.NamingException;

import org.apache.commons.lang.StringEscapeUtils;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Game;

public class ViewGame {
	private final static AbstractDBConnector db = DBConnectorFactory.getDBConnector();

	private String name = "";
	private Game game = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Game getGame() throws NamingException, SQLException {
		if(game == null){
			game = db.getGame(name);
		}
		return game;
	}
	
	public String getGameDescription() throws NamingException, SQLException {
		
		//		return new KIFSyntaxFormatter(getGame().getKIFGameDescription()).getFormattedGameDescription();
		return StringEscapeUtils.escapeHtml(getGame().getGameDescription())
			.replaceAll(" ", "&nbsp;")
			.replace("\n", "<br/>");
	}
}
