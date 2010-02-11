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

package tud.ggpserver.filter;

import java.util.LinkedList;
import java.util.regex.Pattern;

import tud.ggpserver.filter.DropDownMenu.Option;
import tud.ggpserver.util.IdPool;

public abstract class StringMatchFilterRule extends FilterRule{
	
	private static final String IS = "is"; 
	private static final String ISNOT = "is not"; 

	protected DropDownMenu isMenu;
	protected TextBox patternTextBox;
	protected Pattern pattern;

	public StringMatchFilterRule(IdPool<FilterNode> ids, FilterType type) {
		super(ids, type);

		LinkedList<Option> options = new LinkedList<Option>();
		options.add(new Option(IS));
		options.add(new Option(ISNOT));
		
		isMenu = new DropDownMenu(options, String.valueOf(getID()));
		isMenu.setSelectedValue(IS);
		
		patternTextBox = new TextBox(String.valueOf(getID()));
		patternTextBox.setValue("");

		init_matcher();
	}
	
	private void init_matcher() {
		String patternString;
		patternString = patternTextBox.getValue().replace('?', '.');
		patternString = patternString.replace("*", ".*");
		pattern = Pattern.compile(patternString);
		
	}

	public boolean isMatching(String s) {
		return (!patternMatches(s)) ^ isMenu.getSelectedValue().equals(IS); // (matches && "is") || (!matches && !"is")   
	}

	public boolean patternMatches(String s) {
		return pattern.matcher(s).matches();
	}

	@Override
	public boolean update(String[] values) {
		if (super.update(values)) // type has changed
			return true;

		boolean changed = false;
		if (!isMenu.getSelectedValue().equals(values[1])) {
			isMenu.setSelectedValue(values[1]);
			changed = true;
		};
		
		if (!values[2].equals(patternTextBox.getValue())) {
			patternTextBox.setValue(values[2]);
			init_matcher();
			changed = true;
		}
		return changed;
	}

	@Override
	public String getHtml() {
		return super.getHtml() + isMenu.getHtml() + patternTextBox.getHtml();
	}
}
