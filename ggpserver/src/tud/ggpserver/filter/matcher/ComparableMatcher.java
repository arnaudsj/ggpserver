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

public abstract class ComparableMatcher<T extends Comparable<T>> extends Matcher<Comparison, T> {

	public ComparableMatcher(String id, Comparison comparison, T pattern) {
		super(id, comparison, pattern);
	}

	@Override
	public List<Option> getOptions() {
		List<Option> options = new LinkedList<Option>();
		for (Comparison type : Comparison.values()) {
			options.add(new Option(type.getName(), type.toString()));
		}
		return options;
	}

	public boolean isMatching(T t) {
		T pattern = getPattern();
		if (pattern == null) return true; // pattern == null means "*"
		if (t == null) return false;
		switch(getComparison()){
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
	public boolean setComparisonFromString(String comparison) {
		try {
			return setComparison(Comparison.valueOf(comparison));
		} catch (IllegalArgumentException ex) {
			addErrorMessage(ex.getMessage());
		}
		return false;
	}

}
