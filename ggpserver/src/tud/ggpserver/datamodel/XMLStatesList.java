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

package tud.ggpserver.datamodel;

import java.sql.SQLException;
import java.util.AbstractList;
import java.util.regex.Pattern;

public class XMLStatesList extends AbstractList<String> {
	private final AbstractDBConnector<?, ?> db;
	private final String matchID;
	
	private static final Pattern styleSheetPattern = Pattern.compile("<\\?xml-stylesheet type=\"text/xsl\" href=\"[^\"]*\"\\?>");
	private final String stylesheet;

	public XMLStatesList(final String matchID, final AbstractDBConnector<?, ?> db, String stylesheet) {
		this.db = db;
		this.matchID = matchID;
		this.stylesheet = stylesheet;
	}

	@Override
	public String get(int stepNumber) {
		try {
			String xmlState = db.getXMLState(matchID, stepNumber);

			// this is a hack to show old matches with the right stylesheets
			// (e.g., if the stylesheet for a game was changed after the match)
			// we just replace the stylesheet information with the current one
			String styleSheetReplacement = "<?xml-stylesheet type=\"text/xsl\" href=\"" + stylesheet + "\"?>";
			
			return styleSheetPattern.matcher(xmlState).replaceFirst(styleSheetReplacement);
		} catch (SQLException e) {
			IndexOutOfBoundsException e2 = new IndexOutOfBoundsException(e.getMessage());
			e2.initCause(e);
			throw e2;
		}
	}

	@Override
	public int size() {
		try {
			return db.getNumberOfXMLStates(matchID);
		} catch (SQLException e) {
			InternalError internalError = new InternalError(e.getMessage());
			internalError.initCause(e);
			throw internalError;
		}
	}
}
