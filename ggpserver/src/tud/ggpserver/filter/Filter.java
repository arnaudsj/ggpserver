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

import java.lang.ref.SoftReference;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;

import tud.gamecontroller.auxiliary.Pair;
import tud.ggpserver.datamodel.DBConnector;
import tud.ggpserver.datamodel.MatchInfo;
import tud.ggpserver.util.IdPool;

/**
 * 
 * implements a match filter
 *
 * A Filter is actually the root of a tree of FilterNodes where the leafs are FilterRules and the internal nodes are FilterOperations (and, or).
 * The root of the tree is a degenerated FilterAndOperation with at most one successor.  
 */
public class Filter extends FilterANDOperation {

	/**
	 * a reference to the list of MatchIDs that result from the filter<br/>
	 * only used for caching, call getSelectedMatchIds() instead if using this field directly<br/>
	 * is set to null if the filter is changed on update (resetUserData())
	 */
	private SoftReference<List<String>> selectedMatchIdsReference = null;
	
	private String name;

	public Filter(String name) {
		this(new IdPool<FilterNode>(), name);
	}

	public Filter(IdPool<FilterNode> ids, String name) {
		super(ids);
		this.filter = this;
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	/**
	 * @return the html representation of the filter
	 */
	@Override
	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<div id=\"div_filter\">\n");
		sb.append("<span class=\"heading\">").append(getName()).append(":").append("</span>\n");
		if(successors.size()>0) {
			sb.append(successors.get(0).getHtml());
		}else{
			// this is necessary because the newNodeMenu has to be the second value with the id getID() 
			sb.append("<input type=\"hidden\" name=\"").append(getId()).append("\" value=\"").append(getType()).append("\">\n");
			sb.append(newNodeMenu.getHtml());
		}
		sb.append("</div>\n");
		return sb.toString();
	}
	
//	public Object getUserData() {
//		return userData;
//	}
//
//	public void setUserData(Object userData) {
//		this.userData = userData;
//	}
	
	public void resetUserData() {
		selectedMatchIdsReference = null;
	}

	@Override
	public void addSuccessor(FilterNode node) {
		if (successors.size()>0)
			throw new UnsupportedOperationException("Filter root node can have at most one successor!");
		super.addSuccessor(node);
	}

	/**
	 * The methods returns the matches in the database that the filter matches.
	 * This method internally caches results and may not always return the current state of the database.
	 * Calling resetUserData() before getSelectedMatchIDs() makes sure that the result reflects the current state. 
	 * @return a list of match ids
	 * @throws SQLException
	 */
	public List<String> getSelectedMatchIDs() throws SQLException {
		List<String> selectedMatchIds = null;
		if (selectedMatchIdsReference != null) {
			selectedMatchIds = selectedMatchIdsReference.get();
		}
		if (selectedMatchIds == null) {
			List<MatchInfo> matchInfos = getMatchInfos();
			selectedMatchIds = new ArrayList<String>(matchInfos.size());
			for (MatchInfo matchInfo : matchInfos) {
				selectedMatchIds.add(matchInfo.getMatchID());
			}
			selectedMatchIdsReference = new SoftReference<List<String>>(Collections.unmodifiableList(selectedMatchIds));
		}
		return selectedMatchIds;
	}
	
	public List<MatchInfo> filterMatchInfos(List<MatchInfo> matchInfos) {
		List<MatchInfo> selectedMatchInfos = new ArrayList<MatchInfo>();
		for (MatchInfo matchInfo : selectedMatchInfos) {
			if (filter.isMatching(matchInfo))
				selectedMatchInfos.add(matchInfo);
		}
		return selectedMatchInfos;
	}

	/**
	 * 
	 * @return all MatchInfos in the database that the filter matches
	 * @throws SQLException 
	 */
	public List<MatchInfo> getMatchInfos() throws SQLException {
		Pair<List<MatchInfo>, Boolean> selectedMatchInfosPair = DBConnector.getInstance().getMatchInfos(this);
		List<MatchInfo> result = selectedMatchInfosPair.getLeft();
		if(!selectedMatchInfosPair.getRight()) { // don't apply the filter again, if the database already did the matching
			ListIterator<MatchInfo> it = result.listIterator();
			while(it.hasNext()) {
				if (!isMatching(it.next())) {
					it.remove();
				}
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @return true if the filter may be changed
	 */
	public boolean isEditable() {
		return true;
	}

	@Override
	public String toString() {
		String s="FilterRoot[id:"+getId()+", successor:";
		for(FilterNode n:successors) {
			s += n.toString();
		}
		s+="]";
		return s;
	}
}
