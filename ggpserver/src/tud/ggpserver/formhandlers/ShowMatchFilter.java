/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 
                  2010 Peter Steinke <peter.steinke@inf.tu-dresden.de>
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

package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

import tud.ggpserver.datamodel.matches.ServerMatch;
import tud.ggpserver.filter.Filter;

public class ShowMatchFilter extends ShowMatches {
	
	/**
	 * the match filter
	 */
	private Filter filter = null;

	/**
	 * list of MatchIDs that result from the filter<br/>
	 * only used for caching, call getSelectedMatchIds() instead if using this field directly
	 */
	private List<String> selectedMatchIds = null;

	public ShowMatchFilter() { }

	public String getFilterHtml() {
		return filter.getHtml();
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}

	public long getFilterId() {
		return filter.getId();
	}
	
	public Filter getFilter() {
		return filter;
	}
	
	@Override
	public List<? extends ServerMatch<?, ?>> getMatches() throws SQLException {
		List<String> selectedMatchIDs = getSelectedMatchIDs();
		List<ServerMatch<?, ?>> result = new LinkedList<ServerMatch<?, ?>>();
		for (int j = getStartRow(); j <= getEndRow(); ++j) {
			result.add(db.getMatch(selectedMatchIDs.get(j)));
		}
		return result;
	}
	
	private List<String> getSelectedMatchIDs() throws SQLException {
		if (selectedMatchIds == null)
			selectedMatchIds = filter.getSelectedMatchIDs();
		return selectedMatchIds;
	}
	
	/*------- for pager ---------*/
	@Override
	public String getTargetJsp() {
		return "show_filter.jsp?showFilter=false&applyFilter=true&filterId="+getFilterId();
	}

	@Override
	public int getRowCount() throws SQLException {
		return getSelectedMatchIDs().size();
	}
	/*------- for pager - end ---------*/
}
