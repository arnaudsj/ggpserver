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

package tud.ggpserver.datamodel;

import java.util.Collection;
import java.util.Collections;
import java.util.logging.Logger;

import tud.ggpserver.webapp.UserTracker;


public class User {
	public static final String ADMIN_ROLE = "admin";

	private final String userName;
	private final Collection<String> roleNames;
	private final String emailAddress;
	
	/**
	 * Use DBConnectorFactory.getDBConnector().getUser() instead
	 */
	protected User(final String userName, final String emailAddress, final Collection<String> roleNames) {
		if (userName == null || roleNames == null) {
			throw new NullPointerException();
		}
		this.userName = userName;
		this.emailAddress = emailAddress;
		this.roleNames = Collections.unmodifiableCollection(roleNames);
	}
	
	public String getUserName() {
		return userName;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public Collection<String> getRoleNames() {
		return roleNames;
	}
	
	public boolean hasRole(String role) {
		return roleNames.contains(role);
	}

	public boolean isAdmin() {
		return hasRole(ADMIN_ROLE);
	}

	public boolean isLoggedIn() {
		return UserTracker.getInstance().isLoggedIn(this);
	}

//	/*
//	 * notifying the UserTracker about the logout is delayed for a second because SessionAttributeTracker.attributeRemoved
//	 *  + attributeAdded is called every request and we don't want to constantly add and remove the user.  
//	 */
//	private NotifyLogoutThread notifyLogoutThread = null;
//
//	private static class NotifyLogoutThread extends Thread {
//		private User user;
//		private boolean loggedOut = false;
//
//		NotifyLogoutThread(User user) {
//			this.user = user;
//		}
//
//		@Override
//		public void run() {
//			loggedOut = false;
//			try {
//				wait(1000);
//				loggedOut = true;
//				UserTracker.getInstance().notifyLogout(user);
//			} catch (InterruptedException e) {
//				// logout process stopped
//			}
//		}
//	};
//	
//	public synchronized void scheduleNotifyLogout() {
//		if (notifyLogoutThread == null || !notifyLogoutThread.isAlive()) {
//			Logger.getLogger(User.class.getName()).info(userName);
//			notifyLogoutThread = new NotifyLogoutThread(this);
//			notifyLogoutThread.start();
//		}
//	}
//
//	/**
//	 * stops the currently running notifyLogoutThread (if any)
//	 * returns true if it could be stopped before calling notifyLogout of the UserTracker  
//	 * @return
//	 */
//	public synchronized boolean stopNotifyLogout() {
//		if (notifyLogoutThread != null && notifyLogoutThread.isAlive()) {
//			Logger.getLogger(User.class.getName()).info(userName);
//			notifyLogoutThread.interrupt();
//			try {
//				notifyLogoutThread.join();
//			} catch (InterruptedException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return !notifyLogoutThread.loggedOut;
//		}
//		return false;
//	}

	public void notifyLogin() {
		UserTracker.getInstance().notifyLogin(this);
	}

	public void notifyLogout() {
		UserTracker.getInstance().notifyLogout(this);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final User other = (User) obj;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[User:");
		buffer.append(" userName: ");
		buffer.append(userName);
		buffer.append(" roleNames: ");
		buffer.append(roleNames);
		buffer.append("]");
		return buffer.toString();
	}

}
