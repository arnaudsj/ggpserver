package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import tud.gamecontroller.game.javaprover.Term;
import tud.ggpserver.datamodel.Match;
import cs227b.teamIago.util.GameState;

public class ShowMatches extends AbstractPager {
	private String playerName = null;
	private List<Match<Term, GameState>> matches = null;
	
	public List<Match<Term, GameState>> getMatches() throws NamingException, SQLException {
		if (matches == null) {
			matches = db.getMatches(startRow, numDisplayedRows, getReasonerFactory(), playerName);
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
