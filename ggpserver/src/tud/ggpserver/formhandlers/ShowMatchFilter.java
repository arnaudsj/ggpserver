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
import java.util.ArrayList;

import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import tud.ggpserver.datamodel.MatchInfo;
import tud.ggpserver.datamodel.matches.ServerMatch;
import tud.ggpserver.filter.Filter;

public class ShowMatchFilter extends ShowMatches {
	
	/**
	 * the match filter
	 */
	private Filter filter = null;

	/**
	 * flag that should be set, if the matches resulting from the filter should be shown
	 */
	private boolean showMatches = false;

	/**
	 * list of MatchIDs that result from the filter<br/>
	 * only used for caching, call getSelectedMatchIds() instead if using this field directly
	 */
	private List<String> selectedMatchIds = null;

	private static final Logger logger = Logger.getLogger(ShowMatchFilter.class.getName());

	public ShowMatchFilter() {
		filter =  new Filter();
	}

	public String getFilterHtml() {
		return filter.getHtml();
	}

	public Filter getFilter() {
		return filter;
	}

	@SuppressWarnings("unchecked")
	public void setFilter(Filter filter) {
		this.filter = filter;
		this.selectedMatchIds = (List<String>)filter.getUserData();
	}
	
	@SuppressWarnings("unchecked")
	private List<String> getSelectedMatchIDs() throws SQLException {
		if (selectedMatchIds == null) {
			List<MatchInfo> selectedMatchInfos = db.getMatchInfos();
			selectedMatchIds = new ArrayList<String>();
			for (MatchInfo matchInfo : selectedMatchInfos) {
				if (filter.isMatching(matchInfo))
					selectedMatchIds.add(matchInfo.getMatchID());
			}
			filter.setUserData(selectedMatchIds);
		}
		return selectedMatchIds;
	}

	public boolean getShowMatches() {
		return showMatches;
	}

	public void setShowMatches(boolean showMatches) {
		this.showMatches = showMatches;
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
	
	/*------- for pager ---------*/
	@Override
	public String getTableName() {
		return "not set";
	}

	@Override
	public String getTargetJsp() {
		// TODO Auto-generated method stub
		return "show_filter.jsp?showMatches=true";
	}

	@Override
	public int getRowCount() throws SQLException {
		return getSelectedMatchIDs().size();
	}
	/*------- for pager - end ---------*/
}
