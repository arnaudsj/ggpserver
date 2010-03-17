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

public class TournamentFilterRule extends StringMatchFilterRule{
	
	public TournamentFilterRule(Filter filter) {
		this(filter, true, "*");
	}
	public TournamentFilterRule(Filter filter, boolean isMatch, String pattern) {
		super(FilterType.Tournament, filter, true, pattern);
	}
	
	@Override
	public boolean isMatching(MatchInfo matchInfo) {
		return isMatching(matchInfo.getTournament());
	}

	@Override
	public boolean prepareMatchInfosStatement(String matchTableName, String matchPlayerTableName, StringBuilder where, List<Object> parameters) {
		return prepareMatchInfosStatement(matchTableName+".`tournament_id`", where, parameters);
	}
}
