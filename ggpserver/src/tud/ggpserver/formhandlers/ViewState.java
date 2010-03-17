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
import java.util.List;
import java.util.logging.Logger;

import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.auxiliary.Pair;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.Game;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.matches.ServerMatch;

public class ViewState {
	
	private int stepNumber = -1;
	private ServerMatch<?, ?> match;
	private String roleName = "";
	
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
		logger.info("setRole("+roleName+")");
		this.roleName = roleName;
	}
	
	/* MODIFIED
	 * With GDL-II, we have to store, for each game state a representation of the state
	 * This is because we want to retrieve this representation and be able to easily load it into a reasoner,
	 * 	which will allow us to easily compute what this or that player sees from the game state.
	 */
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
			
			// compute Role object from role name
			this.roleName = ""+this.roleName; // little trick, just in case roleName is null 
			RoleInterface<?> role = null;
			List<? extends RoleInterface<?>> roles = match.getGame().getOrderedRoles();
			for (RoleInterface<?> roletp: roles) {
				if (roletp.toString().toUpperCase().equals(this.roleName.toUpperCase())) {
					role = roletp;
					logger.info("Displaying view of player "+role);
					break;
				}
			}
			// if no role name was given, display the state from random point's of view
			if (role == null) {
				for (RoleInterface<?> roletp: roles) {
					if (roletp.toString().toUpperCase().equals("RANDOM")) {
						role = roletp;
						logger.info("Requested player wasn't found, display view of the RANDOM player");
						break;
					}
				}
			}
			// and finally, if random wasn't part of the game, let's choose the first player
			if (role == null) {
				role = roles.get(0);
				logger.info("Requested player wasn't found, and RANDOM isn't part of the game, displaying view of first player "+role);
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
			
			return match.getXMLViewFor(
					stringStates.get(stepNumber - 1),
					stringMoves,
					(RoleInterface) role,
					gdlVersion);
		} else {
			// this can only happen if the initial state wasn't created yet
			return "match " + match.getMatchID() + " has no state!"; 
		}
	}
}
