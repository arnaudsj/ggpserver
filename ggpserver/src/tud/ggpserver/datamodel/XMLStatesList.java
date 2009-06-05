package tud.ggpserver.datamodel;

import java.sql.SQLException;
import java.util.AbstractList;

public class XMLStatesList extends AbstractList<String> {
	private final AbstractDBConnector<?, ?> db;
	private final String matchID;

	public XMLStatesList(final String matchID, final AbstractDBConnector<?, ?> db) {
		this.db = db;
		this.matchID = matchID;
	}

	@Override
	public String get(int stepNumber) {
		try {
			return db.getXMLState(matchID, stepNumber);
		} catch (SQLException e) {
			IndexOutOfBoundsException e2 = new IndexOutOfBoundsException(e.getMessage());
			e2.initCause(e);
			throw e2;
		}
	}

	@Override
	public int size() {
		try {
			return db.getNumberOfXMLStates(matchID);
		} catch (SQLException e) {
			InternalError internalError = new InternalError(e.getMessage());
			internalError.initCause(e);
			throw internalError;
		}
	}
}
