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

package tud.ggpserver.filter;

import java.util.List;

import tud.ggpserver.datamodel.MatchInfo;
import tud.ggpserver.util.IdPool;

/**
 * 
 * a filter that is unchangable and matches everything
 */
public final class MatchAllFilter extends Filter {

	private static final String NAME = "match all"; 
	
	public MatchAllFilter() {
		super(NAME);
	}

	public MatchAllFilter(IdPool<FilterNode> ids) {
		super(ids, NAME);
	}

	/**
	 * @return the html representation of the filter
	 */
	@Override
	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<div id=\"div_filter\">\n");
		sb.append("The filter returns every match.");
		sb.append("</div>\n");
		return sb.toString();
	}
	
	@Override
	public void addSuccessor(FilterNode node) {
		throw new UnsupportedOperationException("Filter root node can have at most one successor!");
	}

	@Override
	public List<MatchInfo> filterMatchInfos(List<MatchInfo> matchInfos) {
		return matchInfos;
	}

	@Override
	public boolean isEditable() {
		return false;
	}
	
	@Override
	public String toString() {
		return "MatchAllFilter[id:"+getId()+"]";
	}
}
