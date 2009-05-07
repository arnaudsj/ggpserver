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
import java.util.List;
import java.util.regex.Pattern;

import tud.ggpserver.datamodel.AbstractDBConnector;
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
		AbstractDBConnector<?, ?> db = DBConnectorFactory.getDBConnector();
		Match<?, ?> match = db.getMatch(matchID);
		// the detour via db is needed here because the stylesheet for a game might
		// have changed in the database, but the match still references an old Game object 
		String stylesheet = db.getGame(match.getGame().getName()).getStylesheet();
		List<String> states = match.getXmlStates();

		// this is a hack to show old matches with the right stylesheets (e.g., if the stylesheet for a game was changed after the match)
		// we just replace the stylesheet information with the current one  
		Pattern styleSheetPattern=Pattern.compile("<\\?xml-stylesheet type=\"text/xsl\" href=\"[^\"]*\"\\?>");
		String styleSheetReplacement="<?xml-stylesheet type=\"text/xsl\" href=\""+stylesheet+"\"?>";
		
		int stepNumber;
		if (this.stepNumber < 1 || this.stepNumber > states.size()) {
			// return the last/final state
			stepNumber = states.size();
		} else {
			stepNumber = this.stepNumber;
		}
		
		return styleSheetPattern.matcher(states.get(stepNumber - 1)).replaceFirst(styleSheetReplacement);
	}
}
