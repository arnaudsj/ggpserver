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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import tud.ggpserver.util.IdPool;

public class FilterSet {

	private HashMap<Long, Filter> filters;
	
	private List<Filter> sortedFilters = null;
	
	private IdPool<FilterNode> ids;
	
	// private static final Logger logger = Logger.getLogger(FilterSet.class.getName());
	
	private int nextFilterNumber = 1;

	public FilterSet() {
		ids = new IdPool<FilterNode>();
		filters = new HashMap<Long, Filter>();
		addFilter(new MatchAllFilter(ids));
	}

	public synchronized Filter addNewFilter() {
		Filter f = new Filter(ids, "filter "+nextFilterNumber);
		nextFilterNumber++;
		addFilter(f);
		return f;
	}

	protected synchronized void addFilter(Filter f) {
		sortedFilters = null;
		filters.put(f.getId(), f);
	}

	public synchronized Filter getFilter(long id) {
		return filters.get(id);
	}

	public synchronized Filter getFirstFilter() {
		return filters.entrySet().iterator().next().getValue();
	}
	
	public synchronized void deleteFilter(long id) {
		sortedFilters = null;
		Filter f = filters.remove(id);
		f.dispose();
		if (filters.isEmpty()) {
			addNewFilter();
		}
	}

	/**
	 * updates the FilterNode with the specified id
	 * @param id
	 * @param values
	 */
	public synchronized boolean update(long id, String[] values) {
		// logger.info("update("+id+","+Arrays.toString(values)+")");
		boolean changed = false;
		FilterNode node = ids.getItem(id);
		if (node!=null) { // if it is null it got probably deleted
			if (node.update(values)) {
				node.getFilter().resetUserData();
				changed = true;
			}
		}
		return changed;
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

	public List<Filter> getFilters() {
		if (sortedFilters == null) {
			List<Filter> filterList = new ArrayList<Filter>(filters.values());
			Collections.sort(filterList, new FilterComparator());
			sortedFilters = Collections.unmodifiableList(filterList);	
		}
		return sortedFilters;
	}

	public final class FilterComparator implements Comparator<Filter> {
		@Override
		public int compare(Filter o1, Filter o2) {
			if (o1.getId()<o2.getId())
				return -1;
			else if(o1.getId()>o2.getId())
				return 1;
			else
				return 0;
		}
	}
}
