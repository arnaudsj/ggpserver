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
import tud.ggpserver.util.IdPool;

public class FilterANDOperation extends FilterOperation{

	public FilterANDOperation(Filter filter) {
		this(filter, null);
	}
	public FilterANDOperation(Filter filter, Collection<FilterNode> successors) {
		super(FilterType.And, filter, successors);
	}
	/**
	 * Using this constructor will leave the filter field of FilterNode null which may cause NullPointerExceptions.
	 * Therefore, it is protected and only used in the class Filter. 
	 * @param ids
	 */
	protected FilterANDOperation(IdPool<FilterNode> ids) {
		super(ids, FilterType.And, null, null);
	}

	@Override
	public boolean isMatching(MatchInfo matchInfo) {
		for (FilterNode node : successors) {
			if (!node.isMatching(matchInfo))
				return false;
		}
		return true;
	}

	@Override
	public boolean prepareMatchInfosStatement(String matchTableName, String matchPlayerTableName, StringBuilder where, List<Object> parameters) {
		boolean equivalent = true;
		if (getSuccessors().size() == 1) {
			equivalent &= getSuccessors().get(0).prepareMatchInfosStatement(matchTableName, matchPlayerTableName, where, parameters);
		} else {
			where.append(" (TRUE");
			for(FilterNode n:getSuccessors()) {
				where.append(" AND");
				equivalent &= n.prepareMatchInfosStatement(matchTableName, matchPlayerTableName, where, parameters);
			}
			where.append(" )");
		}
		return equivalent;
	}
}
