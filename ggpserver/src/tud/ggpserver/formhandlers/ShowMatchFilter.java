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

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.logging.Logger;

import tud.ggpserver.datamodel.MatchInfo;
import tud.ggpserver.datamodel.matches.ServerMatch;
import tud.ggpserver.filter.Filter;
import tud.ggpserver.filter.FilterSet;

public class ShowMatchFilter extends ShowMatches {
	
	/**
	 * the match filters
	 */
	private FilterSet filterSet = null;
	
	private Filter filter = null;
	private long filterID = -1;

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

	public ShowMatchFilter() { }

	public String getFilterHtml() {
		return filter.getHtml();
	}

	public FilterSet getFilterSet() {
		if (filterSet == null) {
			setFilterSet(new FilterSet());
		}
		return filterSet;
	}

	public void setFilterSet(FilterSet filterSet) {
		logger.info("setFilterSet("+filterSet+")");
		this.filterSet = filterSet;
		setFilter(filterSet.getFirstFilter());
	}

	public void setFilterID(long id) {
		logger.info("setFilterID("+id+")");
		filter = filterSet.getFilter(id);
		setFilter(filter);
	}

	public long getFilterID() {
		return filterID;
	}
	
	@SuppressWarnings("unchecked")
	public void setFilter(Filter f) {
		filter = f;
		if( filter == null ) {
			filter = filterSet.getFirstFilter();
		}
		filterID = filter.getID();
		this.selectedMatchIds = (List<String>)filter.getUserData();
	}
	
	public Filter getFilter() {
		return filter;
	}
	
	public Collection<Long> getFilterIDs() {
		return filterSet.getFilterIDs();
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

	public void parseParameterMap(Map<String, String[]> parameterMap) throws SQLException {
		logger.info("parseParameterMap()");
		if (filterSet != null) {
			String[] filterIDList = parameterMap.get("filterID");
			if (filterIDList != null && filterIDList.length>0) {
				try {
					setFilterID(Long.valueOf(filterIDList[0]));
				} catch (NumberFormatException ex) {}
			}
			Set<Entry<String,String[]>> entrySet = parameterMap.entrySet();
			for (Entry<String, String[]> entry : entrySet) {
				String key = entry.getKey();
				if (key.equals("show_matches")) {
					logger.info("show_matches="+Arrays.toString(entry.getValue()));
					showMatches = true;
				}else if (key.equals("delete_filter")) {
					logger.info("delete_filter="+Arrays.toString(entry.getValue()));
					filterSet.deleteFilter(filterID);
					setFilter(filterSet.getFirstFilter());
				}else if (key.equals("add_new_filter")) {
					logger.info("add_new_filter="+Arrays.toString(entry.getValue()));
					setFilter(filterSet.addNewFilter());
				} else {
					String[] values = entry.getValue();
					try {
						filterSet.update(Long.valueOf(key), values);
					} catch (NumberFormatException ex) {}
				}
			}
		}
	}
	
	/*------- for pager ---------*/
	@Override
	public String getTableName() {
		return "not set";
	}

	@Override
	public String getTargetJsp() {
		return "show_filter.jsp?showMatches=true&filterID="+getFilterID();
	}

	@Override
	public int getRowCount() throws SQLException {
		return getSelectedMatchIDs().size();
	}
	/*------- for pager - end ---------*/
}
