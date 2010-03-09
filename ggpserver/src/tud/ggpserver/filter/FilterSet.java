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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Logger;

import tud.ggpserver.util.IdPool;

public class FilterSet {

	private HashMap<Long, Filter> filters;
	
	private IdPool<FilterNode> ids;
	
	private static final Logger logger = Logger.getLogger(FilterSet.class.getName());

	public FilterSet() {
		ids = new IdPool<FilterNode>();
		filters = new HashMap<Long, Filter>();
		addNewFilter();
	}

	public synchronized Filter addNewFilter() {
		Filter f = new Filter(ids);
		filters.put(f.getID(), f);
		return f;
	}
	
	public synchronized Filter getFilter(long id) {
		return filters.get(id);
	}

	public synchronized Filter getFirstFilter() {
		return filters.entrySet().iterator().next().getValue();
	}
	
	public synchronized void deleteFilter(long id) {
		Filter f = filters.remove(id);
		f.dispose();
	}

	/**
	 * updates the FilterNode with the specified id
	 * @param id
	 * @param values
	 */
	public synchronized void update(long id, String[] values) {
		logger.info("update("+id+","+Arrays.toString(values)+")");
		FilterNode node = ids.getItem(id);
		if (node!=null) { // if it is null it got probably deleted
			if (node.update(values)) {
				node.getFilter().resetUserData();
			}
		}
	}

	@Override
	public String toString() {
		String s="FilterSet[filters:";
		for(Filter f:filters.values()) {
			s += f.toString();
		}
		s+="]";
		return s;
	}

	public Collection<Long> getFilterIDs() {
		return Collections.unmodifiableSet(filters.keySet());
	}

}
