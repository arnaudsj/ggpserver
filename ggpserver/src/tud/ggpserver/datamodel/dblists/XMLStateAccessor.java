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

package tud.ggpserver.datamodel.dblists;

import java.sql.SQLException;
import java.util.List;
import java.util.regex.Pattern;

import tud.ggpserver.datamodel.AbstractDBConnector;

public class XMLStateAccessor implements DBAccessor<String> {
	private final AbstractDBConnector<?, ?> db;
	private final String matchID;
	
	private static final Pattern styleSheetPattern = Pattern.compile("<\\?xml-stylesheet type=\"text/xsl\" href=\"[^\"]*\"\\?>");
	private final String stylesheet;

	public XMLStateAccessor(final String matchID, final AbstractDBConnector<?, ?> db, String stylesheet) {
		this.db = db;
		this.matchID = matchID;
		this.stylesheet = stylesheet;
	}


	public List<String> getAllElements() throws SQLException {
		return db.getXMLStates(matchID);
	}

	public String getElement(int stepNumber) throws SQLException {
		String xmlState = db.getXMLState(matchID, stepNumber + 1);   // stepNumber starts from 0, in DB from 1

		// this is a hack to show old matches with the right stylesheets
		// (e.g., if the stylesheet for a game was changed after the match)
		// we just replace the stylesheet information with the current one
		String styleSheetReplacement = "<?xml-stylesheet type=\"text/xsl\" href=\"" + stylesheet + "\"?>";
		
		return styleSheetPattern.matcher(xmlState).replaceFirst(styleSheetReplacement);
	}

	public int getSize() throws SQLException {
		return db.getNumberOfXMLStates(matchID);
	}

}
