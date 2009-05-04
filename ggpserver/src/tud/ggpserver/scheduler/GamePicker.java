package tud.ggpserver.scheduler;

import java.sql.SQLException;
import java.util.List;

import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.Game;

public class GamePicker<TermType extends TermInterface, ReasonerStateInfoType> {
	private Game<TermType, ReasonerStateInfoType> currentGame;
	private final AbstractDBConnector dbConnector;

	public GamePicker(final AbstractDBConnector dbConnector) {
		this.dbConnector = dbConnector;
		try {
			initCurrentGame();
		} catch (SQLException e) {
			throw new InternalError("Could not get games from database! " + e);
		}
	}

	@SuppressWarnings("unchecked")
	private void initCurrentGame() throws SQLException {
		List<Game<TermType, ReasonerStateInfoType>> allGames = getDBConnector().getAllEnabledGames();
		// TODO: start first game with fewest matches (to evenly distribute matches among games)
		this.currentGame = allGames.get(0);
	}
	
	@SuppressWarnings("unchecked")
	public Game<TermType, ReasonerStateInfoType> pickNextGame() throws SQLException {
		Game<TermType, ReasonerStateInfoType> result = currentGame;
		
		List<Game<TermType, ReasonerStateInfoType>> allGames = getDBConnector().getAllEnabledGames();
		
		int nextGameIndex = (allGames.indexOf(currentGame) + 1) % allGames.size();
		currentGame = allGames.get(nextGameIndex);
		
		return result;
	}

	private AbstractDBConnector getDBConnector() {
		return dbConnector;
	}
}
