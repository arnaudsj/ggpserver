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

import java.util.logging.Logger;

import tud.ggpserver.util.IdPool;

/**
 * 
 * implements a match filter
 *
 * A Filter is actually the root of a tree of FilterNodes where the leafs are FilterRules and the internal nodes are FilterOperations (and, or).
 * The root of the tree has always exactly one successor. (i.e., it is a degenerated FilterAndOperation)  
 */
public class Filter extends FilterANDOperation {
	/**
	 * can be used to store some data associated with this filter</br>
	 * is set to null if the filter is changed on update
	 */
	private Object userData = null;
	
	public Filter() {
		super(new IdPool<FilterNode>());
	}

	/**
	 * @return the html representation of the filter
	 */
	@Override
	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<div id=\"div_filter\">");
		sb.append(successors.get(0).getHtml());
		sb.append("</div>");
		return sb.toString();
	}
	
	/**
	 * updates the FilterNode with the specified id
	 * @param id
	 * @param values
	 */
	public void update(Long id, String[] values) {
		FilterNode node = ids.getItem(id);
		if (node!=null) { // if it is null it got probably deleted
			if (node.update(values)) {
				// only set to null if something has changed
				userData = null;
			}
		}
	}

	@Override
	public boolean update(String[] values) {
		throw new UnsupportedOperationException("Filter.update: root node of the Filter is fixed");
	}

	public Object getUserData() {
		return userData;
	}

	public void setUserData(Object userData) {
		this.userData = userData;
	}
	
	@Override
	public synchronized void removeSuccessor(FilterNode node) {
		super.removeSuccessor(node);
		if (successors.isEmpty()) {
			addSuccessor(FilterFactory.getDefaultNode(ids));
		}
	}

	// TODO: override all addSuccessor/deleteSuccessor operations to throw UnsupportedOperationException

	@Override
	public String toString() {
		String s="FilterRoot[id:"+getID()+", successor:";
		for(FilterNode n:successors) {
			s += n.toString();
		}
		s+="]";
		return s;
	}
}
