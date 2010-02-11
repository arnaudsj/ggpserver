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

import java.util.List;


/**
 * represents a select box in an html form
 *
 */
public class DropDownMenu extends HtmlForm {

	public static class Option {
		public String name;
		public String value;
		
		public Option(String name) {
			this(name, name);
		}

		public Option(String name, String value) {
			this.name = name;
			this.value = value;
		}
	}

	private List<Option> options;
	private String selectedValue;


	public String getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(String selectedValue) {
		this.selectedValue = selectedValue;
	}

	public DropDownMenu(List<Option> options, String id) {
		super();
		this.options = options;
		this.id = id;
		this.selectedValue = "";
	}

	public DropDownMenu(List<Option> options, String selectedValue, String id) {
		super();
		this.options = options;
		this.selectedValue = selectedValue;
		this.id = id;
	}

	@Override
	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<select name=\"").append(id).append("\"").append(onChange).append(">");
		for (Option option : options) {
			sb.append("<option");
			if (option.value.equals(selectedValue)) {
				 sb.append(" selected=\"selected\"");
			}
			sb.append(" value=\"").append(option.value).append("\">").append(option.name).append("</option>\n");
		}
		sb.append("</select>");
		return sb.toString();
	}
	
}
