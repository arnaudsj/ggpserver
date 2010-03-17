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

import tud.ggpserver.datamodel.MatchInfo;
import tud.ggpserver.filter.Filter;
import tud.ggpserver.filter.matcher.Comparison;
import tud.ggpserver.filter.matcher.LongMatcher;
import tud.ggpserver.filter.matcher.Matcher;

public class PlayClockFilterRule extends LongMatchFilterRule{
	
	public PlayClockFilterRule(Filter filter) {
		super(FilterType.PlayClock, filter);
	}
	public PlayClockFilterRule(Filter filter, Comparison comparison, Long pattern) {
		super(FilterType.StartClock, filter, comparison, pattern);
	}
	
	@Override
	public Matcher<Comparison, Long> createMatcher(Comparison comparison, Long pattern) {
		return new LongMatcher(String.valueOf(getId()),comparison,pattern,0,Long.MAX_VALUE);
	}

	@Override
	public boolean isMatching(MatchInfo matchInfo) {
		return isMatching(matchInfo.getPlayclock().longValue());
	}

	@Override
	public boolean prepareMatchInfosStatement(String matchTableName, String matchPlayerTableName, StringBuilder where, List<Object> parameters) {
		return prepareMatchInfosStatement(matchTableName+".`start_clock`", where, parameters);
	}
}
