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

package tud.ggpserver.filter.rules;

import tud.ggpserver.filter.Filter;
import tud.ggpserver.filter.matcher.Matcher;

public abstract class MatchFilterRule<ComparisonType, PatternType> extends FilterRule{
	
	protected Matcher<ComparisonType, PatternType> matcher;

	public MatchFilterRule(FilterType type, Filter filter, ComparisonType comparison, PatternType pattern) {
		super(type, filter);
		this.matcher = createMatcher(comparison, pattern);
	}
	
	public abstract Matcher<ComparisonType, PatternType> createMatcher(ComparisonType comparison, PatternType pattern);
	
	public Matcher<ComparisonType, PatternType> getMatcher() {
		return matcher;
	}
	
	@Override
	public boolean update(String[] values) {
		if (super.update(values)) // type has changed
			return true;
		if(values.length>=3) {
			return matcher.update(values[1], values[2]);
		}else{
			return false;
		}
	}

	@Override
	public String getHtml() {
		return super.getHtml() + matcher.getHtml();
	}
	
	public boolean isMatching(PatternType t) {
		return matcher.isMatching(t);
	}
	
	@Override
	public String toString() {
		return "MatchFilterRule[type:"+getType()+ ", "+matcher.toString()+"]";
	}
}
