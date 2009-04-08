package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.List;

import javax.naming.NamingException;

import tud.gamecontroller.game.javaprover.Term;
import tud.ggpserver.datamodel.Game;
import cs227b.teamIago.util.GameState;

public class ShowGames extends AbstractPager {
	public List<Game<Term, GameState>> getGames() throws NamingException, SQLException {
		return db.getGames(startRow, numDisplayedRows, getReasonerFactory());
	}

	@Override
	public String getTargetJsp() {
		return "show_games.jsp";
	}

	@Override
	protected String getTableName() {
		return "games";
	}
}
