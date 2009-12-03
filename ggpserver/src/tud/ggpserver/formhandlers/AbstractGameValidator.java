/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 

    This file is part of GGP Server.

    GGP Server is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GGP Server is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GGP Server.  If not, see <http://www.gnu.org/licenses/>.
*/

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

import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.javaprover.Reasoner;
import tud.gamecontroller.game.javaprover.Term;
import cs227b.teamIago.parser.Axioms;
import cs227b.teamIago.util.GameState;

import static tud.ggpserver.datamodel.DBConnectorFactory.getDBConnector;

public abstract class AbstractGameValidator {
	/*
	 * These fields must either be initialized with a non-null value, or we need
	 * to explicitly do an if-check for != null inside isValid() (see
	 * assertions).
	 * 
	 * For example, gameDescription would remain "null" if setGameName() is
	 * called with a non-existing game name.
	 */
	private String gameName = "";
	private String gameDescription = "";
	private String stylesheet = "../stylesheets/generic/generic.xsl";
	private boolean enabled = false;
	
	private List<String> errorsGameName = new LinkedList<String>();
	private List<String> errorsDescription = new LinkedList<String>();
	private List<String> errorsStylesheet = new LinkedList<String>();
	
	public AbstractGameValidator() {
		Logger.getLogger(Axioms.class.getName()).addHandler(new Handler() {
			@Override
			public void close() {
			}

			@Override
			public void flush() {
			}

			@Override
			public void publish(LogRecord record) {
				getErrorsDescription().add(record.getMessage());
			}
		});		
	}
	
	public boolean isValid() throws SQLException {
		///// game name ////
		getErrorsGameName().clear();
		
		assert (gameName != null);
		
		if (gameName.equals("")) {
			getErrorsGameName().add("game name must not be empty");
		}
		if (gameName.length() > 40) {
			getErrorsGameName().add("game name must not be longer than 40 characters");
		}
		if (!gameName.matches("[a-zA-Z0-9._-]*")) {
			getErrorsGameName().add("game name must only contain the following characters: a-z A-Z 0-9 . _ -");
		}
		boolean gameExists = (getDBConnector().getGame(gameName) != null);
		if (gameExists && !isGameExpectedToExist()) {			// the game must not exist already
			getErrorsGameName().add("there is already a game with name '" + gameName + "', please pick a different one");
		} else if (!gameExists && isGameExpectedToExist()) {	// the game has to exist already
			getErrorsGameName().add("there is no game with name '" + gameName + "'");
		}
		
		//// game description////
		errorsDescription.clear();
		
		assert(gameDescription != null);
		
		if (gameDescription.equals("")) {
			errorsDescription.add("description must not be empty");
		}
		// it would be really nice if the parser could check the validity of the
		// game description here. since it can't, at least we can assure that
		// the legal roles can be correctly created and so on.
		try {
			Reasoner reasoner = new Reasoner(gameDescription);
			
			GameState initialState = reasoner.getInitialState();
			if (initialState == null) {
				errorsDescription.add("there was a syntax error in your game description (could not get initial state)");
			}
			List<? extends RoleInterface<Term>> roles = reasoner.getRoles();
			if (roles == null || roles.isEmpty()) {
				errorsDescription.add("there was a syntax error in your game description (could not determine number of roles)");
			} else {
				for (RoleInterface<Term> role : roles) {
					Collection<? extends MoveInterface<Term>> legalMoves = reasoner.getLegalMoves(initialState, role);
					if (legalMoves == null || legalMoves.isEmpty()) {
						errorsDescription.add("there was a syntax error in your game description (could not get legal moves)");
					}
				}
			}
		} catch (RuntimeException e1) {
			// JavaProver likes to throw NullPointerExceptions when called with
			// game descriptions such as "asdsdfsdfsdfg", so all we can do here
			// is catch RuntimeExceptions.
			errorsDescription.add("there was an error in your game description, JavaProver threw:" + e1.toString());
		}
		
		//// stylesheet ////
		errorsStylesheet.clear();
		try {
			assert (stylesheet != null);
			
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
		
		if (getErrorsGameName().size() > 0) {
			result = false;
		}
		if (errorsDescription.size() > 0) {
			result = false;
		}
		if (errorsStylesheet.size() > 0) {
			result = false;
		}
		
		return result;
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

	public boolean getEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
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

	/**
	 * @return <code>true</code> if the game MUST exist <br>
	 *         <code>false</code> if the game MUST NOT exist
	 */
	protected abstract boolean isGameExpectedToExist();
}
