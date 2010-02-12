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

package tud.ggpserver.filter.htmlform;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;


/**
 * represents a select box in an html form
 *
 */
public class DropDownMenu extends HtmlForm {

	public static class Option {
		/**
		 * the name of the option, i.e., what is shown on the web page
		 */
		public String name; 

		/**
		 * the value of the option, i.e., what is sent with the http-request
		 * 
		 * The value must be a string of characters that does not have to be escaped. I.e., it must not contain &quot;, &lt;, etc. 
		 */
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
	private boolean doSubmitOnChange = false;

	public DropDownMenu(String id, List<Option> options) {
		this(id, options, options.get(0).value);
	}

	public DropDownMenu(String id, List<Option> options, String selectedValue) {
		super(id);
		this.options = options;
		this.selectedValue = selectedValue;
	}

	public boolean isSubmitOnChange() {
		return doSubmitOnChange;
	}

	public void setSubmitOnChange(boolean submitOnChange) {
		this.doSubmitOnChange = submitOnChange;
	}

	public String getSelectedValue() {
		return selectedValue;
	}

	public void setSelectedValue(String selectedValue) {
		this.selectedValue = selectedValue;
	}

	@Override
	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<select name=\"").append(StringEscapeUtils.escapeHtml(getId())).append("\"");
		if(doSubmitOnChange) sb.append(submitOnChange);
		sb.append(">");
		for (Option option : options) {
			sb.append("<option");
			if (option.value.equals(selectedValue)) {
				 sb.append(" selected=\"selected\"");
			}
			sb.append(" value=\"").append(option.value).append("\">").append(StringEscapeUtils.escapeHtml(option.name)).append("</option>\n");
		}
		sb.append("</select>");
		return sb.toString();
	}
	
}
