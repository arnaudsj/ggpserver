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

import tud.ggpserver.datamodel.MatchInfo;
import tud.ggpserver.filter.FilterNode;
import tud.ggpserver.util.IdPool;

public class DefaultFilterRule extends FilterRule {

	public DefaultFilterRule(IdPool<FilterNode> ids) {
		super(ids, FilterType.Default);
	}

	public boolean isMatching(MatchInfo matchInfo) {
		if(isRoot()) { // default filter is true for an "and", false for an "or" and true if it is the root
			return true;
		}
		return getParent().getType().equals(FilterType.And); 
	}
}
