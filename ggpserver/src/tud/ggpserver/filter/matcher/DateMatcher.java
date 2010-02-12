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

package tud.ggpserver.filter.matcher;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import tud.ggpserver.filter.htmlform.DropDownMenu;
import tud.ggpserver.filter.htmlform.TextBox;

public class DateMatcher extends ComparableMatcher<Date> {

	private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss z", Locale.US);

	public DateMatcher(String id) {
		super(id);
	}
	
	@Override
	protected DropDownMenu createMenu() {
		DropDownMenu menu = super.createMenu();
		menu.setSelectedValue(Comparison.SmallerEqual.toString());
		return menu;
	}

	@Override
	protected TextBox createTextBox() {
		return new TextBox(getId(), dateFormat.format(new Date()));
	}

	@Override
	protected Date parsePattern(String pattern) throws IllegalArgumentException {
		Date d = null;
		try{
			d = dateFormat.parse(pattern);
		} catch(Exception ex) {
			d = null;
		}
		if(d == null) {
			throw new IllegalArgumentException("Invalid date format! (use e.g., \""+dateFormat.format(new Date())+"\")");
		}
		return d;
	}

	public DateFormat getDateFormat() {
		return dateFormat;
	}
}
