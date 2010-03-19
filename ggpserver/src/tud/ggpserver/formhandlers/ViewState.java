/*
    Copyright (C) 2009-2010 Martin Günther <mintar@gmx.de> 
                  2010 Nicolas JEAN <njean42@gmail.com>
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
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.XMLGameStateWriter;
import tud.gamecontroller.auxiliary.Pair;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.impl.Game;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.matches.ServerMatch;

public class ViewState {
	
	private int stepNumber = -1;
	private ServerMatch<? extends TermInterface, ?> match;
	private String roleName = null;
	
	private static final Logger logger = Logger.getLogger(Game.class.getName());
	

	public void setMatchID(String matchID) throws SQLException {
		match = DBConnectorFactory.getDBConnector().getMatch(matchID);
		if (match == null) {
			throw new NullPointerException();
		}
	}

	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}
	
	public void setRole(String roleName) {
		this.roleName = roleName;
	}
	
	@SuppressWarnings("unchecked")
	public String getXmlState() {
		
		int stepNumber = this.stepNumber;
		List<Pair<Date,String>> stringStates = match.getStringStates();
		
		int numberOfStates = stringStates.size();
		if(numberOfStates > 0) {
			if (stepNumber < 1 || stepNumber > numberOfStates) {
				// return the last/final state
				stepNumber = numberOfStates;
			}
			Pair<Date,String> stringState = stringStates.get(stepNumber-1);
			
			GameInterface game = match.getGame();
			RoleInterface role = null;

			// compute Role object from role name
			if (roleName != null) {
				role = game.getRoleByName(roleName);
			}
			if (role == null) {
				role = game.getNatureRole();
			}
			
			// get history of moves
			List<List<String>> stringMoves = match.getJointMovesStrings();
			logger.info("stringMoves = "+stringMoves);
			//while (stringMoves.size() > stepNumber) {
			if (stringMoves.size() > 0)
				stringMoves = stringMoves.subList(0, stepNumber-1);
			//}
			
			GDLVersion gdlVersion = match.getGame().getGdlVersion();
			logger.info("gdlVersion = "+gdlVersion);
			
			StateInterface state = match.getGame().getStateFromString(stringState.getRight());
			
			// get goal values
			Map<? extends RoleInterface, Integer> goalValues = null;
			if (state.isTerminal()) {
				goalValues = match.getGoalValues();
			}
			
			return XMLGameStateWriter.createXMLOutputStream(
					match,
					state,
					stringMoves, // moves...
					goalValues,
					match.getGame().getStylesheet(),
					role,
					stringState.getLeft()
				).toString();

//			return match.getXMLViewFor(
//					stringStates.get(stepNumber - 1),
//					stringMoves,
//					(RoleInterface) role,
//					gdlVersion);
		} else {
			// this can only happen if the initial state wasn't created yet
			return "match " + match.getMatchID() + " has no state!"; 
		}
	}
}
