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

/**
 * represents a text box in an html form
 *
 */
public class TextBox extends HtmlForm{

	private static final int DEFAULTSIZE = 10;
	private static final int MAXSIZE = 25;
		
	private String value;
	private int size;
	private int maxLength;

	public TextBox(String id) {
		this(id, "");
	}

	public TextBox(String id, String value) {
		this(id, value, -1, -1);
	}

	/**
	 * @param id name used for the form element
	 * @param value initial value of the text box
	 * @param size size of the textbox (-1 for automatic size)
	 * @param maxLength maximal length of the input (-1 for no limit)
	 */
	public TextBox(String id, String value, int size, int maxLength) {
		super(id);
		this.value = value;
		this.size = size;
		this.maxLength = maxLength;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<input type=\"text\" name=\"").append(StringEscapeUtils.escapeHtml(getId())).append("\"");
		int realSize = size;
		if (realSize<1) {
			if(maxLength!=-1 && maxLength<=DEFAULTSIZE){
				// maxLength is small -> size=maxLength 
				realSize = maxLength;
			}else{
				// no maxLength or maxLength is large -> size is at least DEFAULTSIZE but may be larger if value is larger 
				realSize = Math.max(DEFAULTSIZE, value.length());
			}
			realSize = Math.min(MAXSIZE, realSize);
		}
		sb.append(" size=\"").append(realSize).append("\"");
		if (maxLength>-1) {
			sb.append(" maxlength=\"").append(maxLength).append("\"");
		}
		sb.append(" value=\"").append(StringEscapeUtils.escapeHtml(value)).append("\" />");
		return sb.toString();
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
		if(maxLength>-1 && value.length()>maxLength) {
			value = value.substring(0, maxLength-1);
		}
	}

	public void setSize(int size) {
		this.size = size;
	}

}
