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

import java.util.List;

import tud.ggpserver.filter.Filter;
import tud.ggpserver.filter.matcher.Comparison;
import tud.ggpserver.filter.matcher.LongMatcher;
import tud.ggpserver.filter.matcher.Matcher;

public abstract class LongMatchFilterRule extends MatchFilterRule<Comparison, Long>{
	
	public LongMatchFilterRule(FilterType type, Filter filter) {
		this(type, filter, Comparison.Equal, null);
	}
	public LongMatchFilterRule(FilterType type, Filter filter, Comparison comparison, Long pattern) {
		super(type, filter, comparison, pattern);
	}

	@Override
	public Matcher<Comparison, Long> createMatcher(Comparison comparison, Long pattern) {
		return new LongMatcher(String.valueOf(getId()), comparison, pattern);
	}

	/**
	 * @see FilterNode.prepareMatchInfosStatement
	 * @param tableColumnName
	 * @param where
	 * @param parameters
	 * @return
	 */
	public boolean prepareMatchInfosStatement(String tableColumnName, StringBuilder where, List<Object> parameters) {
		if (getMatcher().getPattern() != null) {
			where.append(" ").append(tableColumnName)
				.append(getMatcher().getComparison().getSQLOperator())
				.append("?");
			parameters.add(getMatcher().getPattern());
		}
		return true;
	}

}
