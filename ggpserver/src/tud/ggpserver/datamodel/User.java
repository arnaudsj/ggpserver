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

import java.util.Collection;
import java.util.Collections;


public class User {
	private final String userName;
	private final Collection<String> roleNames;
	
	/**
	 * Use DBConnectorFactory.getDBConnector().getUser() instead
	 */
	protected User(final String userName, final Collection<String> roleNames) {
		if (userName == null || roleNames == null) {
			throw new NullPointerException();
		}
		this.userName = userName;
		this.roleNames = Collections.unmodifiableCollection(roleNames);
	}
	
	public String getUserName() {
		return userName;
	}

	public Collection<String> getRoleNames() {
		return roleNames;
	}
	
	public boolean hasRole(String role) {
		return roleNames.contains(role);
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
