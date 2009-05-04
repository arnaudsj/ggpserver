package tud.ggpserver.formhandlers;

import static tud.ggpserver.datamodel.DBConnectorFactory.getDBConnector;

import java.sql.SQLException;

import tud.ggpserver.datamodel.Game;

public class EditGame extends AbstractGameValidator {
	private boolean correctlyUpdated = false;
	
	public void updateGame() throws SQLException {
		assert(isValid());
		// TODO: fill in (???)
		getDBConnector().updateGameInfo(getGameName(), getGameDescription(), getStylesheet(), getEnabled());
		correctlyUpdated = true;
	}
	
	public boolean isCorrectlyUpdated() {
		return correctlyUpdated;
	}

	public void setGameName(String gameName) {
		super.setGameName(gameName);
		try {
			Game game = getDBConnector().getGame(gameName);
			if (game == null) {
				getErrorsGameName().add("there is no game with name '" + gameName + "'");
			} else {
				setGameDescription(game.getGameDescription());
				setStylesheet(game.getStylesheet());
				setEnabled(game.isEnabled());
			}
		} catch (SQLException e) {
			getErrorsGameName().add("there was an error getting game '" + gameName + "'"
					+ " from the database: " + e.getMessage());
		}
	}

	@Override
	protected boolean isGameExpectedToExist() {
		return true;
	}
}
