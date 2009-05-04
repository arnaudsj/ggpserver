package tud.ggpserver.formhandlers;

import static tud.ggpserver.datamodel.DBConnectorFactory.getDBConnector;

import java.sql.SQLException;

import tud.ggpserver.datamodel.DuplicateInstanceException;

public class CreateGame extends AbstractGameValidator {
	private boolean correctlyCreated = false;
	
	public void create() throws SQLException {
		assert(isValid());
		try {
			getDBConnector().createGame(getGameDescription(), getGameName(), getStylesheet(), getEnabled());
			correctlyCreated = true;
		} catch (DuplicateInstanceException e) {
			getErrorsGameName().add("game name already exists, please pick a different one");
			correctlyCreated = false;
		}
	}
	
	public boolean isCorrectlyCreated() {
		return correctlyCreated;
	}

	@Override
	protected boolean isGameExpectedToExist() {
		return false;
	}
}
