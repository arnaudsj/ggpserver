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
import java.util.LinkedList;
import java.util.List;

import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.DuplicateInstanceException;

public class Register {
	private List<String> errorsPassword1 = new LinkedList<String>();
	private List<String> errorsPassword2 = new LinkedList<String>();
	private List<String> errorsEmailAddress = new LinkedList<String>();
	private List<String> errorsUserName = new LinkedList<String>();

	private String userName = "";
	private String password1 = "";
	private String password2 = "";
	private String emailAddress = "";
	
	private boolean correctlyCreated = false;
	
	public String getPassword1() {
		return password1;
	}

	public String getPassword2() {
		return password2;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public String getUserName() {
		return userName;
	}

	public void setPassword1(String p1) {
		password1 = p1;
	}

	public void setPassword2(String p2) {
		password2 = p2;
	}

	public void setEmailAddress(String e) {
		emailAddress = e;
	}

	public void setUserName(String u) {
		userName = u;
	}

	public boolean isValid() throws SQLException {
		/* user name */
		errorsUserName.clear();
		if (userName.equals("")) {
			errorsUserName.add("user name must not be empty");
		}
		if (userName.length() > 20) {
			errorsUserName.add("user name must not be longer than 20 characters");
		}
		if (!userName.matches( "[a-zA-Z][a-zA-Z0-9._-]*" )) {
			errorsUserName.add("user name must begin with a letter and only contain the following characters: a-z A-Z 0-9 . _ -");
			// do NOT allow "<" or ">" for the user name (otherwise cross-site scripting possible)
		} else if (DBConnectorFactory.getDBConnector().getUser(userName) != null) {
			// this is an "else if" such that only valid user names are checked to prevent SQL injection
			errorsUserName.add("user name already exists, please pick a different one");
		}
		   
		/* password 1 */
		errorsPassword1.clear();
		if (password1.equals("")) {
			errorsPassword1.add("password must not be empty");
		}
		if (password1.length() > 20) {
			errorsPassword1.add("password must not be longer than 20 characters");
		}
		if (!password1.matches( "[[\\p{Alnum}\\p{Punct}]&&[^\"'\\\\`]]*" )) {
			// every alphanumeric or punctuation character except: " ' \ ` (to prevent SQL injection)
			// strictly speaking, this is not necessary any more because passwords are now hashed before
			// being stored into the database. however, it must still not contain `"`, because of the html form
			errorsPassword1.add("password can only contain the following characters: a-z A-Z 0-9 !#&sect;%&amp;()*+,-./:;&lt;=&gt;?@[]^_{}|~");
		}

		/* password 2 */
		errorsPassword2.clear();
		if (!password2.equals(password1)) {
			errorsPassword2.add("passwords don't match, please repeat password");
		}
		if (!password2.matches( "[[\\p{Alnum}\\p{Punct}]&&[^\"'\\\\`]]*" )) {
			// validate confirmation of password because of html form problem
			password2 = "";
		}

		/* email address */
		errorsEmailAddress.clear();
		if (emailAddress.equals("")) {
			errorsEmailAddress.add("email address must not be empty");
		}
		if (emailAddress.length() > 320) {
			errorsEmailAddress.add("email address must not be longer than 320 characters");
		}
		if (!isValidEmailAddress(emailAddress)) {
			errorsEmailAddress.add("invalid email address");
		}

		/* check for errors */
		boolean result = true;

		if (errorsUserName.size() > 0) {
			userName = "";
			result = false;
		}
		if (errorsPassword1.size() > 0) {
			password1 = "";
			result = false;
		}
		if (errorsPassword2.size() > 0) {
			password2 = "";
			result = false;
		}
		if (errorsEmailAddress.size() > 0) {
			result = false;
		}
		
		return result;
	}
	
	/**
	 * Saves the user information to the database. Also sets correctlyCreated to
	 * true if successful, false if user name already present.
	 * 
	 * @throws SQLException 
	 */
	public void createUser() throws SQLException {
		try {
			DBConnectorFactory.getDBConnector().createUser(userName, password1, emailAddress);
			correctlyCreated = true;
		} catch (DuplicateInstanceException e) {
			errorsUserName.add("user name already exists, please pick a different one");
			correctlyCreated = false;
		}
	}
	
	public boolean isCorrectlyCreated() {
		return correctlyCreated;
	}

	public List<String> getErrorsPassword1() {
		return errorsPassword1;
	}

	public List<String> getErrorsPassword2() {
		return errorsPassword2;
	}

	public List<String> getErrorsEmailAddress() {
		return errorsEmailAddress;
	}

	public List<String> getErrorsUserName() {
		return errorsUserName;
	}

	/**
	  * Validate the form of an email address.
	  *
	  * <P>Return <tt>true</tt> only if 
	  *<ul> 
	  * <li> <tt>aEmailAddress</tt> can successfully construct an 
	  * {@link javax.mail.internet.InternetAddress} 
	  * <li> when parsed with "@" as delimiter, <tt>aEmailAddress</tt> contains 
	  * two tokens which satisfy {@link hirondelle.web4j.util.Util#textHasContent}.
	  *</ul>
	  *
	  *<P> The second condition arises since local email addresses, simply of the form
	  * "<tt>albert</tt>", for example, are valid for 
	  * {@link javax.mail.internet.InternetAddress}, but almost always undesired.
	  * 
	  * from http://www.javapractices.com/topic/TopicAction.do?Id=180
	  */
	public static boolean isValidEmailAddress(String emailAddressString){
		if (emailAddressString == null) return false;
		boolean result = true;
		try {
			/* InternetAddress emailAddr = */ new InternetAddress(emailAddressString);
			if ( !hasNameAndDomain(emailAddressString) ) {
				result = false;
			}
		}
		catch (AddressException ex){
			result = false;
		}
		return result;
	}
	
	private static boolean hasNameAndDomain(String aEmailAddress){
		String[] tokens = aEmailAddress.split("@");
	    return tokens.length == 2 && !tokens[0].trim().isEmpty() && !tokens[1].trim().isEmpty();
	}

}
