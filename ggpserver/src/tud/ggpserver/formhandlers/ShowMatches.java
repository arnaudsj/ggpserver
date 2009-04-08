package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import tud.gamecontroller.game.javaprover.Term;
import tud.ggpserver.datamodel.Match;
import cs227b.teamIago.util.GameState;

public class ShowMatches extends AbstractPager {
	public List<Match<Term, GameState>> getMatches() throws NamingException, SQLException {
		return db.getMatches(startRow, numDisplayedRows, getReasonerFactory());
	}
	
	@Override
	protected String getTableName() {
		return "matches";
	}

	@Override
	public String getTargetJsp() {
		return "show_matches.jsp";
	}

}
