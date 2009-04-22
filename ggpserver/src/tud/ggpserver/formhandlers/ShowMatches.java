package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Match;

public class ShowMatches extends AbstractPager {
	private String playerName = null;
	private List<Match> matches = null;
	protected final static AbstractDBConnector db = DBConnectorFactory.getDBConnector();
	
	@SuppressWarnings("unchecked")
	public List<Match> getMatches() throws NamingException, SQLException {
		if (matches == null) {
			matches = db.getMatches(startRow, numDisplayedRows, playerName);
		}
		return matches;
	}
	
	@Override
	protected int getRowCount() throws NamingException, SQLException {
		if (playerName == null) {
			return super.getRowCount();
		} else {
			return db.getRowCountPlayerMatches(playerName);
		}
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	@Override
	protected String getTableName() {
		return "matches";
	}

	@Override
	public String getTargetJsp() {
		if (playerName == null) {
			return "show_matches.jsp";
		} else {
			return "show_matches.jsp?playerName=" + playerName;
		}
	}

}
