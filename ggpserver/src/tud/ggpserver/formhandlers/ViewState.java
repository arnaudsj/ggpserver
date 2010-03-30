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
import java.util.logging.Logger;

import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.auxiliary.Pair;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.matches.ServerMatch;
import tud.ggpserver.util.StateXMLExporter;

public class ViewState {
	
	private int stepNumber = -1;
	private ServerMatch<?, ?> match;
	private String roleName = null;
	private String userName;
	
	public void setMatchID(String matchID) throws SQLException {
		match = DBConnectorFactory.getDBConnector().getMatch(matchID);
		if (match == null) {
			throw new NullPointerException();
		}
	}
	
	public String getMatchID () {
		return match.getMatchID();
	}
	
	public void setUserName (String userName) {
		this.userName = userName;
	}

	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}
	
	public void setRole(String roleName) {
		this.roleName = roleName;
	}
	
	public boolean isViewable () {
		if ( match.getGame().getGdlVersion() == GDLVersion.v1 ) return true; // if it is a GDL-I game, i.e. with complete information, anybody is allowed to see everything
		String ownerName = match.getOwner().getUserName();
		if (userName != null &&
			userName.equals(ownerName) &&
			! match.getOrderedPlayerNames().contains(ownerName) ) return true; // allow visibility to the creator of the game, but not if he also plays (he's a human, thus could use info he or she should not see)
		if ( match.getStatus().equals(ServerMatch.STATUS_RUNNING) ) return false; // if GDL-II, and match running, nobody should have access to all of the information 
		return true;
	}
	
	public String getXmlState() {
		List<Pair<Date,String>> stringStates = match.getStringStates();
		Logger.getLogger(ViewState.class.getName()).info("StateXMLExporter.getStepXML(match, stringStates, "+stepNumber+", roleName)");
		return StateXMLExporter.getStepXML(match, stringStates, stepNumber, roleName);
	}
}
