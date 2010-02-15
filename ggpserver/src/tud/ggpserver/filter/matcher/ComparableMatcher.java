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

import java.util.LinkedList;
import java.util.List;

import tud.ggpserver.filter.htmlform.DropDownMenu.Option;

public abstract class ComparableMatcher<T extends Comparable<T>> extends Matcher<T> {

	public static enum Comparison {
		Equal("="), NotEqual("!="), Greater(">"), GreaterEqual(">="), Smaller("<"), SmallerEqual("<=");
		private String name;
		private Comparison(String name) {
			this.name = name;
		}
		public String getName(){
			return name;
		}
	};

	protected T pattern;

	public ComparableMatcher(String id) {
		super(id);
	}

	@Override
	public List<Option> getOptions() {
		List<Option> options = new LinkedList<Option>();
		for (Comparison type : Comparison.values()) {
			options.add(new Option(type.getName(), type.toString()));
		}
		return options;
	}

	/**
	 * 
	 * @param pattern string
	 * @return Object of type T
	 */
	protected abstract T parsePattern(String pattern) throws IllegalArgumentException;
	
	@Override
	protected void initMatcher() {
		if (patternTextBox.getValue().equals("*")) {
			pattern = null; 
		} else {
			try{
				pattern = parsePattern(patternTextBox.getValue());
			} catch(IllegalArgumentException ex) {
				pattern = null;
				addErrorMessage(ex.getMessage());
			}
		}
	}

	public boolean isMatching(T t) {
		if (patternTextBox.getValue().equals("*")) return true;
		if (pattern == null || t == null) return false;
		switch(Comparison.valueOf(comparisonMenu.getSelectedValue())){
			case Equal: return t.compareTo(pattern) == 0;
			case NotEqual: return t.compareTo(pattern) != 0;
			case Greater: return t.compareTo(pattern) > 0;
			case GreaterEqual: return t.compareTo(pattern) >= 0;
			case Smaller: return t.compareTo(pattern) < 0;
			case SmallerEqual: return t.compareTo(pattern) <= 0;
		}
		return false;
	}

	@Override
	public String toString() {
		return comparisonMenu.getSelectedValue()+" "+pattern;
	}

}
