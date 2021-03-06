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

import java.util.Collection;
import java.util.List;

import tud.ggpserver.datamodel.MatchInfo;

public class FilterOROperation extends FilterOperation{

	public FilterOROperation(Filter filter) {
		this(filter, null);
	}
	public FilterOROperation(Filter filter, Collection<FilterNode> successors) {
		super(FilterType.Or, filter, successors);
	}
	
	@Override
	public boolean isMatching(MatchInfo matchInfo) {
		for (FilterNode node : successors) {
			if (node.isMatching(matchInfo))
				return true;
		}
		
		return false;
	}

	@Override
	public boolean prepareMatchInfosStatement(String matchTableName, String matchPlayerTableName, StringBuilder where, List<Object> parameters) {
		boolean equivalent = true;
		if (getSuccessors().size() == 1) {
			equivalent &= getSuccessors().get(0).prepareMatchInfosStatement(matchTableName, matchPlayerTableName, where, parameters);
		} else {
			where.append(" (FALSE");
			for(FilterNode n:getSuccessors()) {
				where.append(" OR");
				equivalent &= n.prepareMatchInfosStatement(matchTableName, matchPlayerTableName, where, parameters);
			}
			where.append(" )");
		}
		return equivalent;
	}

}
