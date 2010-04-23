/*
    Copyright (C) 2010 Stephan Schiffel <stephan.schiffel@gmx.de>

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

import java.security.Principal;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import javax.security.auth.Subject;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionAttributeListener;
import javax.servlet.http.HttpSessionBindingEvent;

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnector;
import tud.ggpserver.datamodel.User;

/**
 * tracks session attributes in order to detect login of users
 * 
 * Specifically, SessionAttributeTracker checks the javax.security.auth.subject attribute and calls User.notifyLogin.
 */
public class SessionAttributeTracker implements HttpSessionAttributeListener {

	public static final String SESSION_USER_ATTRIBUTE = "tud.ggpserver.datamodel.User";
	
	private static Logger logger = Logger.getLogger(SessionAttributeTracker.class.getName());
	private AbstractDBConnector<?, ?> dbConnector = DBConnector.getInstance();
	
	public SessionAttributeTracker() { }

	private User userAttribute(HttpSessionBindingEvent se) {
		User user = null;
		String name = se.getName();
		Object value = se.getValue();
		if (name.equals("javax.security.auth.subject") && value instanceof Subject) {
			Subject subject = (Subject)value;
			Set<Principal> principals = subject.getPrincipals();
			Iterator<Principal> it = principals.iterator(); 
			if(it.hasNext()) {
				if(principals.size() > 1) {
					logger.warning("Multiple principals, only the first one is used: " + principals);
				}
				Principal firstPrincipal = it.next();
				try {
					user = dbConnector.getUser(firstPrincipal.getName());
				} catch (SQLException e) {
					e.printStackTrace();
					logger.severe("Probably invalid userName: " + firstPrincipal.getName() + "! SQLException: " + e);
				}
			}
		}
		return user;
	}


	public void attributeAdded(HttpSessionBindingEvent se) {
		HttpSession session = se.getSession();
		User user = (User)session.getAttribute(SESSION_USER_ATTRIBUTE); 
		if (user == null) {
			user = userAttribute(se);
			if (user != null) {
				session.setAttribute(SESSION_USER_ATTRIBUTE, user);
				user.notifyLogin();
			}
		}
	}

	public void attributeRemoved(HttpSessionBindingEvent se) {
		if (se.getName().equals(SESSION_USER_ATTRIBUTE)) {
			User user = (User)se.getValue();
			user.notifyLogout();
		}
	}

	public void attributeReplaced(HttpSessionBindingEvent se) { }

}