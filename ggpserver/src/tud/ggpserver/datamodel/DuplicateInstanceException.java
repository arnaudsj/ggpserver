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

/**
 * This Exception is thrown whenever a new instance of an object (e.g., user) in
 * the MYSQL Database should be created whose primary key (e.g., user name)
 * already exists.
 * 
 * @author martin
 */
public class DuplicateInstanceException extends Exception {
	private static final long serialVersionUID = 385501759945166543L;

	public DuplicateInstanceException() {
	}

	public DuplicateInstanceException(String message) {
		super(message);
	}

	public DuplicateInstanceException(Throwable cause) {
		super(cause);
	}

	public DuplicateInstanceException(String message, Throwable cause) {
		super(message, cause);
	}
}
