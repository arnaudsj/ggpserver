/*
    Copyright (C) 2010 Peter Steinke <peter.steinke@inf.tu-dresden.de>

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

package tud.ggpserver.filter;

/**
 * represents a text box in an html form
 *
 */
public class TextBox extends HtmlForm{
	private String value;

	public TextBox(String value, String id) {
		super();
		this.value = value;
		this.id = id;
	}

	public TextBox(String id) {
		super();
		this.id = id;
		this.value = "";
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getHtml() {
		return "<input type=\"text\" name=\"" + id + "\" size=\"10\" value=\"" + value + "\" />";
	}

}
