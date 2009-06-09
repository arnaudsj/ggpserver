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

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Tournament;

public class ShowTournaments extends AbstractPager {

	@SuppressWarnings("unchecked")
	public List<Tournament> getTournaments() throws SQLException {
		AbstractDBConnector db = DBConnectorFactory.getDBConnector();
		return db.getTournaments();
	}
	
	
	@Override
	public String getTableName() {
		return "tournaments";
	}

	@Override
	public String getTargetJsp() {
		return "show_tournaments.jsp";
	}
}
