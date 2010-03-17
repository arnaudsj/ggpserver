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

package tud.ggpserver.filter.rules;

import java.util.List;

import tud.ggpserver.filter.Filter;
import tud.ggpserver.filter.FilterNode;
import tud.ggpserver.filter.matcher.Matcher;
import tud.ggpserver.filter.matcher.StringMatcher;

public abstract class StringMatchFilterRule extends MatchFilterRule<Boolean, String>{
	
	public StringMatchFilterRule(FilterType type, Filter filter, boolean isMatch, String pattern) {
		super(type, filter, isMatch, pattern);
	}
	
	@Override
	public Matcher<Boolean, String> createMatcher(Boolean isMatch, String pattern) {
		return new StringMatcher(String.valueOf(getId()), isMatch, pattern);
	}

	public boolean patternMatches(String s) {
		return ((StringMatcher)matcher).patternMatches(s);
	}

	/**
	 * @see FilterNode.prepareMatchInfosStatement
	 * @param tableColumnName
	 * @param where
	 * @param parameters
	 * @return
	 */
	public boolean prepareMatchInfosStatement(String tableColumnName, StringBuilder where, List<Object> parameters) {
		where.append(" ").append(tableColumnName);
		if (!getMatcher().getComparison())
			where.append(" NOT");
		where.append(" LIKE ?");
		String pattern = getMatcher().getPattern();
		String sqlPattern = pattern.replace("%", "\\%").replace("_", "\\_").replace('*', '%').replace('?', '_');
		parameters.add(sqlPattern);
		return true;
	}

}
