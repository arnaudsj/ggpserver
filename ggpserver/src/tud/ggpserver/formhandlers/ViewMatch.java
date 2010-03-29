/*
    Copyright (C) 2009-2010 Martin Günther <mintar@gmx.de>
                  2010 Nicolas JEAN <njean42@gmail.com>

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
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.HumanPlayerInfo;
import tud.ggpserver.datamodel.RemotePlayerInfo;
import tud.ggpserver.datamodel.matches.ServerMatch;
import tud.ggpserver.scheduler.MatchRunner;
import tud.ggpserver.util.Utilities;


public class ViewMatch {
	private static final Logger logger = Logger.getLogger(ViewMatch.class.getName());
	
	private String matchID;
	private ServerMatch<?, ?> match;
	private int stepNumber = 1;
	private String playerName = null;
	private List<GameControllerErrorMessage> errorMessagesForStep = null;
	private String userName;

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getStepNumber() {
		return stepNumber;
	}

	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
		errorMessagesForStep = null;
	}

	public void setMatchID(String matchID) throws SQLException {
		this.matchID = matchID;
		match = DBConnectorFactory.getDBConnector().getMatch(matchID);
		errorMessagesForStep = null;
	}
	
	public String getMatchID () {
		return matchID;
	}
	
	public void setuserName (String userName) {
		this.userName = userName;
	}
	
	public List<Pair<String,String>> getStatuses () {
		List<Pair<String,String>> res = new LinkedList<Pair<String,String>>();
		for (PlayerInfo p: match.getOrderedPlayerInfos()) {
			if (p instanceof HumanPlayerInfo) { // human players
				if (MatchRunner.getInstance().hasAccepted(match.getMatchID(), p.getName())) {
					res.add(new Pair<String,String>(p.getName(),"accepted"));
				} else {
					res.add(new Pair<String,String>(p.getName(),"not yet"));
				}
			} else if (p instanceof RemotePlayerInfo) { // remote players
				if (MatchRunner.getInstance().isAvailable(p.getName())) {
					res.add(new Pair<String,String>(p.getName(),"accepted"));
				} else {
					res.add(new Pair<String,String>(p.getName(),"not yet"));
				}
			} else { // random or legal
				res.add(new Pair<String,String>(p.getName(),"accepted"));
			}
		}
		return res;
	}
	
	public ServerMatch<?, ?> getMatch() {
		return match;
	}
	
	/**
	 * 
	 * @return a list of moves as Strings for the current stepNumber and match
	 */
	public List<String> getMoves() {
		if ((stepNumber < 1) || stepNumber > (match.getStringStates().size() - 1)) {  // -1, because there is one less jointmove than states
			return new LinkedList<String>();
		}
		return match.getJointMovesStrings().get(stepNumber - 1);
	}
	
	public boolean isNoMoves () {
		return this.getMoves().isEmpty();
	}
	
	/**
	 * 
	 * @return true if playerName==null or the list of error messages for
	 *         the current stepNumber and match contains a message with the current playerName
	 */
	public boolean hasErrorForPlayer() {
		if(playerName == null)
			return true;
		for(GameControllerErrorMessage msg : getErrorMessages()){
			if(playerName.equals(msg.getPlayerName()))
				return true;
		}
		return false;
	}
	
	/**
	 * 
	 * @return the list of error messages for the current stepNumber and match
	 */
	public List<GameControllerErrorMessage> getErrorMessages() {
		if(errorMessagesForStep==null){
			int numberOfStates = match.getStringStates().size();
			if ((stepNumber < 1) || (stepNumber > numberOfStates)) {
				return new LinkedList<GameControllerErrorMessage>();
			}
			if (numberOfStates > match.getErrorMessages().size()) {
				String message = "getErrorMessages().size() smaller than getNumberOfStates()! Causing match: " + match.toString();
				logger.severe(message);
				throw new InternalError(message);
			}
			errorMessagesForStep = match.getErrorMessages().get(stepNumber - 1);
		}
		return errorMessagesForStep;
	}
	
	public Date getTimestamp () {
		return match.getStringStates().get(stepNumber-1).getLeft();
	}
	
	public int getGdlVersion () {
		return Utilities.gdlVersion(this.match.getGame().getGdlVersion());
	}
	
}
