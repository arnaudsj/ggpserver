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

import java.sql.SQLException;

import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Match;

public class ViewState {
	private String matchID;
	private int stepNumber = -1;

	public void setMatchID(String matchID) {
		this.matchID = matchID;
	}

	public void setStepNumber(int stepNumber) {
		this.stepNumber = stepNumber;
	}

	public String getXmlState() throws SQLException {
		Match<?, ?> match = DBConnectorFactory.getDBConnector().getMatch(matchID);
		
		int stepNumber;
		if (this.stepNumber < 1 || this.stepNumber > match.getNumberOfStates()) {
			// return the last/final state
			stepNumber = match.getNumberOfStates();
		} else {
			stepNumber = this.stepNumber;
		}
		
		return match.getXmlState(stepNumber);
	}
}
