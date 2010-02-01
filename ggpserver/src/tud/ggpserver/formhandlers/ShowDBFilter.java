/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 

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

import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.matches.ServerMatch;

import tud.ggpserver.util.Filter;

import tud.ggpserver.util.MatchInfo;


//public class ShowDBFilter extends AbstractPager {
public class ShowDBFilter {
	private Integer max_page = 1;
	private int selected_page = 1;
	private final static int MAX_ROWS = 20;
	private Filter filter;
	private int show_matches = 0;
	private boolean new_search = true;
	
	public boolean isNew_search() {
		return new_search;
	}
	public void setNew_search(boolean newSearch) {
		new_search = newSearch;
	}
	public int getMax_page() {
		return max_page;
	}
	public void setMax_page(Object maxPage) {
		max_page = (Integer)maxPage;
	}
	public int getPage() {
		return selected_page;
	}
	public void setPage(int selectedPage) {
		selected_page = selectedPage;
	}

	public ShowDBFilter() {
		filter =  new Filter();
	}
	
	public String getTable() {
		return filter.getTable();
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Object filter) {
		this.filter = (Filter)filter;
	}
	
	@SuppressWarnings("unchecked")
	private static final AbstractDBConnector db = DBConnectorFactory.getDBConnector();
	private List<String> selectedMatcheIds;
	
	@SuppressWarnings("unchecked")
	private void selectMatches() {
		List<MatchInfo> selectedMatcheInfos = db.getSelectedMatcheInfos("SELECT mp.match_id,player,roleindex,goal_value,game,start_clock,play_clock,start_time,status FROM match_players AS mp JOIN matches AS m ON mp.match_id = m.match_id ORDER BY mp.match_id");
		selectedMatcheIds = new ArrayList<String>();
		for (MatchInfo matchInfo : selectedMatcheInfos) {
			if (filter.isMatching(matchInfo))
				selectedMatcheIds.add(matchInfo.getMatchID());
		}
	}

	

	public int getShow_matches() {
		return show_matches;
	}


	public void setShow_matches(int showMatches) {
		show_matches = showMatches;
	}
	
	public void init() {
		if (new_search) {
			selectMatches();
			max_page = (int) Math.ceil((float)selectedMatcheIds.size() / (float)MAX_ROWS);
//			filter.setSelectedMatches(selectedMatcheIds);
		}
	}
	
	public List<? extends ServerMatch<?, ?>> getMatches() throws SQLException {
		List<ServerMatch<?, ?>> result = new LinkedList<ServerMatch<?, ?>>();

		int i = 0;
		for (int j = selected_page * MAX_ROWS - MAX_ROWS; j < selected_page * MAX_ROWS; ++j) {
			if (j >= selectedMatcheIds.size())
				break;
			result.add(db.getMatch(selectedMatcheIds.get(j)));
		}
		
		return result;
	}
	
	public String getPageInfo() {
	if (max_page == 1)
		return "";
	
	String pageInfo = "";
	for (int i = 1; i <= max_page; ++i) {
		if (selected_page != i)
			pageInfo += "<a href='show_filter.jsp?page=" + i + "&show_matches=1'>" + i +"</a>\n";
		else
			pageInfo += i + "\n";
	}
	
	return pageInfo;
}
	
	public String getPlayerName() {
		return null;
	}
}
