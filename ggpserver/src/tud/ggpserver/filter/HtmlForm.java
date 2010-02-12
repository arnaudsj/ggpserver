/*
    Copyright (C) 2010 Peter Steinke <peter.steinke@inf.tu-dresden.de>
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

package tud.ggpserver.filter.htmlform;

import org.apache.commons.lang.StringEscapeUtils;

public abstract class HtmlForm {
	protected final static String submitOnChange = " onChange=\"form.submit()\" ";
	protected final static String submitOnClick = " onClick=\"form.submit()\" ";

	private String id;
	private String errorMessage = null;
	
	public HtmlForm(String id) {
		this.id=id;
	}
	
	public String getId() {
		return id;
	}

	public abstract String getHtml();

	public String getErrorHtml() {
		if (errorMessage != null) {
			return "<span class=\"error_msg\">" + StringEscapeUtils.escapeHtml(errorMessage) + "</span>";
		}
		return "";
	}

	public void addErrorMessage(String msg) {
		if (errorMessage == null) {
			errorMessage = msg;
		} else {
			errorMessage = errorMessage + ";" + msg;
		}
	}

	public void resetErrorMessage() {
		errorMessage = null;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

}
