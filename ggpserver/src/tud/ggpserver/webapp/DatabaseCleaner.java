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

package tud.ggpserver.webapp;

import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import tud.ggpserver.datamodel.DBConnectorFactory;

/**
 * On application startup, this class starts the database cleanup function.
 * 
 * @author Martin Günther <mintar@gmx.de>
 *
 */
public class DatabaseCleaner implements ServletContextListener {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(DatabaseCleaner.class.getName());

	public void contextDestroyed(ServletContextEvent event) {
		// nothing to do
	}

	public void contextInitialized(ServletContextEvent event) {
		try {
			DBConnectorFactory.getDBConnector().cleanup();
		} catch (SQLException e) {
			logger.severe("ServletContextEvent - exception: " + e); //$NON-NLS-1$
		}
	}

}
