/*
    Copyright (C) 2010 Nicolas JEAN <njean42@gmail.com>
                  2010 Stephan Schiffel <stephan.schiffel@gmx.de>

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

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import tud.gamecontroller.auxiliary.Pair;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.players.HumanPlayer;
import tud.gamecontroller.players.Player;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.User;
import tud.ggpserver.datamodel.matches.RunningMatch;
import tud.ggpserver.datamodel.matches.ServerMatch;
import tud.ggpserver.scheduler.MatchRunner;
import tud.ggpserver.util.StateXMLExporter;

public class Play {
	
	protected static final Logger logger = Logger.getLogger(Play.class.getName());
	
	private User user = null;
	private String role = null;
	
	private String matchID = null;
	private RunningMatch<?,?> match = null;
	private String matchStatus = null;
	private HumanPlayer<?,?> player = null;
	private int stepNumber;
	
	private MoveInterface<?> move = null;
	private int forStepNumber;
	
	public void setMatchID(String matchID) throws SQLException {
		// set our match
		this.matchID = matchID;
		ServerMatch<?,?> match1 = MatchRunner.getInstance().getMatch(matchID);
		if (match1 != null ) {
			matchStatus = match1.getStatus();
			if (matchStatus.equals(ServerMatch.STATUS_RUNNING)) {
				match = (RunningMatch<?,?>)match1;
			}
			// set stepNumber
			if (match != null)
				stepNumber = match.getCurrentStep();
		}
		logger.info("setMatchID("+matchID+"): " + (match!=null));
	}
	
	public String getMatchID() {
		return matchID;
	}

	public int getStepNumber() {
		return stepNumber;
	}

	public void setUserName(String userName) throws SQLException {
		logger.info("setUser("+userName+")");
		user = DBConnectorFactory.getDBConnector().getUser(userName);
	}
	
	@SuppressWarnings("unchecked")
	public void setRole(String role) {
		logger.info("setRole("+role+")");
		this.role = role;
		// find our human player
		Player playerByRole = match.getPlayer((RoleInterface)match.getGame().getRoleByName(role));
		if (playerByRole!=null && playerByRole instanceof HumanPlayer &&
			user!=null &&
			playerByRole.getName().equals(user.getUserName())) {  // only allow the correct logged-in user to access and play for this role
				player = (HumanPlayer) playerByRole;
		}
		logger.info("player: "+player);
	}
	
	public String getRole() {
		return role;
	}

	public void setForStepNumber(int forStepNumber) {
		this.forStepNumber = forStepNumber;
	}

	@SuppressWarnings("unchecked")
	public void setChosenMove(int i) throws SQLException {
		if (!isPlaying())
			return;
		
		// set move
		try {
			move = player.getLegalMoves().get(i);
		} catch (IndexOutOfBoundsException ex) {
			move = null;
		}
		logger.info("setChosenMove("+move+", " + forStepNumber + ")");
		if (move != null)
			player.setMove( (MoveInterface)move, forStepNumber);
		move = player.getMove();
	}
	
	public void setConfirm(boolean confirm) throws SQLException {
		if (!isPlaying())
			return;
		if (confirm) {
			logger.info("setConfirm(" + forStepNumber + ")");
			player.confirm(forStepNumber);
		}
	}

	public void setQuickConfirm(boolean quickConfirm) throws SQLException {
		if (!isPlaying())
			return;
		logger.info("setQuickConfirm(" + quickConfirm + ")");
		player.setQuickConfirm(quickConfirm);
	}

	/**
	 * @return true if the match is running and the selected role is played by the currently logged in user and at least the initial state of the match is already available 
	 */
	public boolean isPlaying() throws SQLException {
		if (match!=null && player!=null) {
			List<Pair<Date,String>> stringStates = match.getStringStates();
			if (stringStates.isEmpty()) {
				try {
					Thread.sleep(1000); // wait a second to get match started
				} catch (InterruptedException e) {
				}
				return !stringStates.isEmpty();
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
	
	public boolean isFinished() {
		return matchStatus != null && matchStatus.equals(ServerMatch.STATUS_FINISHED);
	}

	public String getXmlState() throws SQLException {
		if(!isPlaying())
			return null;
		List<Pair<Date,String>> stringStates = match.getStringStates();
		return StateXMLExporter.getStepXML(match, stringStates, stepNumber, getRole(), true, player.getQuickConfirm(), getLegalMoves(), getMove(), getConfirmed());
	}

	// private helper functions
	private List<String> getLegalMoves() {
		
		List<? extends MoveInterface<?>> moves = player.getLegalMoves();
		logger.info("moves = "+moves);
		if (moves == null) {
			// TODO: act correspondingly if this returns false
			return null;
		}
		
		List<String> stringMoves = new LinkedList<String>();
		for (MoveInterface<?> move: moves)
			stringMoves.add(move.getKIFForm());
		
		return stringMoves;
		
	}
	
	private String getMove() {
		if (move == null) {
			move = player.getMove();
		}
		if (move != null) {
			return move.getKIFForm();
		} else {
			return null;
		}
	}
	
	private boolean getConfirmed() {
		return player.hasConfirmed(stepNumber);
	}
	
}
