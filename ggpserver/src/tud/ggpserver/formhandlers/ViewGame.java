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

import org.apache.commons.lang.StringEscapeUtils;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Game;

public class ViewGame {
	private final static AbstractDBConnector db = DBConnectorFactory.getDBConnector();

	private String name = "";
	private Game game = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public Game getGame() throws SQLException {
		if(game == null){
			game = db.getGame(name);
		}
		return game;
	}
	
	public String getGameDescription() throws SQLException {
		
		//		return new KIFSyntaxFormatter(getGame().getKIFGameDescription()).getFormattedGameDescription();
		return StringEscapeUtils.escapeHtml(getGame().getGameDescription())
			.replaceAll(" ", "&nbsp;")
			.replace("\n", "<br/>");
	}
}
