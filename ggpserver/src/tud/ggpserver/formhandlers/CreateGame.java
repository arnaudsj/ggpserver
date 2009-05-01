package tud.ggpserver.formhandlers;

import java.net.URI;
import java.net.URISyntaxException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.naming.NamingException;

import cs227b.teamIago.parser.Axioms;
import cs227b.teamIago.util.GameState;

import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.javaprover.Reasoner;
import tud.gamecontroller.game.javaprover.Term;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.DuplicateInstanceException;

public class CreateGame extends Handler {
	private String gameName = "";
	private String gameDescription = "";
	private String stylesheet = "../stylesheets/generic/generic.xsl";

	private List<String> errorsGameName = new LinkedList<String>();
	private List<String> errorsDescription = new LinkedList<String>();
	private List<String> errorsStylesheet = new LinkedList<String>();
	
	private boolean correctlyCreated = false;
	private List<LogRecord> parseLogRecords = new LinkedList<LogRecord>();
	
	private final static AbstractDBConnector db = DBConnectorFactory.getDBConnector();

	
	public CreateGame() {
		super();
		Logger.getLogger(Axioms.class.getName()).addHandler(this);
	}

	public boolean isValid() throws NamingException, SQLException {
		///// game name ////
		errorsGameName.clear();
		if (gameName.equals("")) {
			errorsGameName.add("game name must not be empty");
		}
		if (gameName.length() > 40) {
			errorsGameName.add("game name must not be longer than 40 characters");
		}
		if (!gameName.matches("[a-zA-Z0-9._-]*")) {
			errorsGameName.add("game name must only contain the following characters: a-z A-Z 0-9 . _ -");
		} else if (db.getGame(gameName) != null) {
			// this is an "else if" such that only valid names are checked to prevent SQL injection
			errorsGameName.add("game name already exists, please pick a different one");
		}
		
		//// game description////
		errorsDescription.clear();
		parseLogRecords.clear();
		if (gameDescription.equals("")) {
			errorsDescription.add("description must not be empty");
		}
		// it would be really nice if the parser could check the validity of the
		// game description here. since it can't, at least we can assure that
		// the legal roles can be correctly created and so on.
		try {
			Reasoner reasoner = new Reasoner(gameDescription);
			
			List<? extends RoleInterface<Term>> roles = reasoner.GetRoles();
			if (roles == null || roles.isEmpty()) {
				errorsDescription.add("there was a syntax error in your game description (could not determine number of roles)");
			}
			
			GameState initialState = reasoner.getInitialState();
			if (initialState == null) {
				errorsDescription.add("there was a syntax error in your game description (could not get initial state)");
			}
			
			for (RoleInterface<Term> role : roles) {
				Collection<? extends MoveInterface<Term>> legalMoves = reasoner.GetLegalMoves(initialState, role);
				if (legalMoves == null || legalMoves.isEmpty()) {
					errorsDescription.add("there was a syntax error in your game description (could not get legal moves)");
				}
			}
		} catch (RuntimeException e1) {
			// JavaProver likes to throw NullPointerExceptions when called with
			// game descriptions such as "asdsdfsdfsdfg", so all we can do here
			// is catch RuntimeExceptions.
			errorsDescription.add("there was a syntax error in your game description");
			for (LogRecord record : parseLogRecords) {
				errorsDescription.add(record.getMessage());
			}
		}
		
		//// stylesheet ////
		errorsStylesheet.clear();
		try {
			// only use the path element (throw away scheme, user info, host, port, query and fragment)
			stylesheet = new URI(stylesheet).normalize().getPath();

			if (stylesheet == null || stylesheet.equals("")) {
				// stylesheet == null can happen if the user enters a valid URI that does not contain a path element (such as "mailto:mintar@web.de")
				errorsStylesheet.add("for security reasons, the stylesheet URI must be relative and begin with \"../stylesheets/\"");
			} else {
				if (stylesheet.length() > 255) {
					errorsStylesheet.add("stylesheet URI must not be longer than 255 characters");
				}
				if (!stylesheet.toLowerCase().startsWith("../stylesheets/")) {
					errorsStylesheet.add("for security reasons, the stylesheet URI must be relative and begin with \"../stylesheets/\"");
				}
			}
		} catch (URISyntaxException e) {
			errorsStylesheet.add("stylesheet URI syntax exception: " + e.getMessage());
		}
		
		//// check result ////
		boolean result = true;
		
		if (errorsGameName.size() > 0) {
			gameName = "";
			result = false;
		}
		if (errorsDescription.size() > 0) {
			// don't do that here. If the user spent half an hour typing in a
			// game description and we set it to "", they will kill us.
			// gameDescription = "";
			
			result = false;
		}
		if (errorsStylesheet.size() > 0) {
			stylesheet = "../stylesheets/generic/generic.xsl";
			result = false;
		}
		
		return result;
	}
	
	public void create() throws NamingException, SQLException {
		assert(isValid());
		try {
			db.createGame(gameDescription, gameName, stylesheet);
			correctlyCreated = true;
		} catch (DuplicateInstanceException e) {
			errorsGameName.add("game name already exists, please pick a different one");
			correctlyCreated = false;
		}
	}
	
	public boolean isCorrectlyCreated() {
		return correctlyCreated;
	}

	public String getGameDescription() {
		return gameDescription;
	}

	public void setGameDescription(String gameDescription) {
		this.gameDescription = gameDescription;
	}

	public String getGameName() {
		return gameName;
	}

	public void setGameName(String gameName) {
		this.gameName = gameName;
	}

	public String getStylesheet() {
		return stylesheet;
	}

	public void setStylesheet(String stylesheet) {
		this.stylesheet = stylesheet;
	}

	public List<String> getErrorsDescription() {
		return errorsDescription;
	}

	public List<String> getErrorsGameName() {
		return errorsGameName;
	}

	public List<String> getErrorsStylesheet() {
		return errorsStylesheet;
	}

	@Override
	public void close() throws SecurityException {
	}

	@Override
	public void flush() {
	}

	@Override
	public void publish(LogRecord record) {
		parseLogRecords .add(record);
	}
	
	
}
