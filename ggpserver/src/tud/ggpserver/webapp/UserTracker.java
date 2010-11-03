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

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import tud.ggpserver.datamodel.User;

/**
 * The UserTracker keeps track of logged in users. For this notifyLogin must be called on login
 * and notifyLogout must be called on logout or when the session is destroyed.
 */

public class UserTracker {
	
	private static UserTracker instance = null;
	
	public static synchronized UserTracker getInstance() {
		if (instance == null) {
			instance = new UserTracker();
		}
		return instance;
	}
	
	private Set<User> loggedInUsers;  
	
	protected UserTracker() {
		loggedInUsers = new HashSet<User>();
	}
	
	public Collection<User> getLoggedInUsers() {
		return new HashSet<User>(loggedInUsers);
	}
	
	public boolean isLoggedIn(User user) {
		synchronized (loggedInUsers) {
			return loggedInUsers.contains(user);
		}
	}
	
	public void notifyLogin(User user) {
		Logger.getLogger(UserTracker.class.getName()).info("user: " + user);
		synchronized (loggedInUsers) {
			loggedInUsers.add(user);
		}
	}
	
	public void notifyLogout(User user) {
		Logger.getLogger(UserTracker.class.getName()).info("user: " + user);
		synchronized (loggedInUsers) {
			loggedInUsers.remove(user);
		}
	}
	
}
