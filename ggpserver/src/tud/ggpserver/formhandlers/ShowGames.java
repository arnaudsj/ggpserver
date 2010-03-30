/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 
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
import java.util.List;

import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Game;

public class ShowGames extends AbstractPager {
	
	private List<String> gameNames = null;
	
	public List<Game<?, ?>> getGames() throws SQLException {
		return DBConnectorFactory.getDBConnector().getGames(getStartRow(), getNumDisplayedRows());
	}

	@Override
	public String getTargetJsp() {
		return "show_games.jsp";
	}

	@Override
	public String getTableName() {
		return "games";
	}
	
	@Override
	public String getTitleOfPage(int pageNumber) throws SQLException {
		if (gameNames == null) {
			gameNames = DBConnectorFactory.getDBConnector().getGameNames();
		}
		int firstGameIndex = (pageNumber - 1)*getNumDisplayedRows();
		if (firstGameIndex>=0 && firstGameIndex < gameNames.size())
			return gameNames.get(firstGameIndex) + ", ...";
		else
			return null;
	}
}
