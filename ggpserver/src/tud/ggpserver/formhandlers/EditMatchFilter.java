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

package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import tud.ggpserver.filter.Filter;
import tud.ggpserver.filter.FilterSet;

public class EditMatchFilter {
	
	/**
	 * the match filters
	 */
	private FilterSet filterSet = null;
	
	private Filter filter = null;
	private long filterId = -1;

	/**
	 * flag that should be set, if the matches resulting from the filter should be shown
	 */
	private boolean applyFilter = false;

	/**
	 * flag that should be set to false, if the filter should be hidden
	 */
	private boolean showFilter = true;

	// private static final Logger logger = Logger.getLogger(EditMatchFilter.class.getName());
	
	private List<String> parametersToKeep = new LinkedList<String>(); 

	public EditMatchFilter() { }

	public String getFilterHtml() {
		return filter.getHtml();
	}

	public FilterSet getFilterSet() {
//		if (filterSet == null) {
//			setFilterSet(new FilterSet());
//		}
		return filterSet;
	}

	public void setFilterSet(FilterSet filterSet) {
		// logger.info("setFilterSet("+filterSet+")");
		this.filterSet = filterSet;
		setFilter(filterSet.getFirstFilter());
	}

	public void setFilterId(long id) {
		// logger.info("setFilterID("+id+")");
		filter = filterSet.getFilter(id);
		setFilter(filter);
	}

	public long getFilterId() {
		return filterId;
	}

	public String getSelectedFilterName() {
		return getFilter().getName();
	}

	private void setFilter(Filter f) {
		filter = f;
		if( filter == null ) {
			filter = filterSet.getFirstFilter();
		}
		filterId = filter.getId();
	}
	
	public Filter getFilter() {
		return filter;
	}
	
	public List<Filter> getFilters() {
		return filterSet.getFilters();
	}

	public boolean getApplyFilter() {
		return applyFilter;
	}

	public void setApplyFilter(boolean applyFilter) {
		// logger.info("setApplyFilter("+applyFilter+")");
		this.applyFilter = applyFilter;
	}

	public boolean getShowFilter() {
		return showFilter;
	}

	public void setShowFilter(boolean showFilter) {
		// logger.info("setShowFilter("+showFilter+")");
		this.showFilter = showFilter;
	}
	
	public void setKeepParam(String name) {
		addParameterToKeep(name);
	}

	public void addParameterToKeep(String name) {
		parametersToKeep.add(name);
	}

	public List<String> getParametersToKeep() {
		return parametersToKeep;
	}

	public void parseParameterMap(Map<String, String[]> parameterMap) throws SQLException {
		if (filterSet != null) {
			// somehow it doesn't work to set these properties using jsp:setProperty, so we have to do it here
			// it has to be done before checking if any of the buttons were clicked
			String[] values;
			values = parameterMap.get("filterId");
			if (values != null && values.length>0) {
				try { setFilterId(Long.valueOf(values[0]));	} catch (NumberFormatException ex) {}
			}
			values = parameterMap.get("showFilter");
			if (values != null && values.length>0) {
				setShowFilter(Boolean.valueOf(values[0]));
			}
			values = parameterMap.get("applyFilter");
			if (values != null && values.length>0) {
				setApplyFilter(Boolean.valueOf(values[0]));
			}
			// parse the remaining request parameters (button clicks, filter updates, ...) 
			Set<Entry<String,String[]>> entrySet = parameterMap.entrySet();
			for (Entry<String, String[]> entry : entrySet) {
				String key = entry.getKey();
				values = entry.getValue();
				// logger.info("param: " + key + " => " + Arrays.toString(values));
				if (key.equals("apply_filter") && values.length>0 && !values[0].isEmpty()) {
					setApplyFilter(true);
				}else if (key.equals("show_filter") && values.length>0 && !values[0].isEmpty()) {
					setShowFilter(true);
				}else if (key.equals("hide_filter") && values.length>0 && !values[0].isEmpty()) {
					setShowFilter(false);
					setApplyFilter(true);
				}else if (key.equals("delete_filter") && values.length>0 && !values[0].isEmpty()) {
					filterSet.deleteFilter(filterId);
					setFilter(filterSet.getFirstFilter());
					setApplyFilter(false);
				}else if (key.equals("add_new_filter") && values.length>0 && !values[0].isEmpty()) {
					setFilter(filterSet.addNewFilter());
					setApplyFilter(false);
				} else {
					try {
						if(filterSet.update(Long.valueOf(key), values)) {
							setApplyFilter(false);
						}
					} catch (NumberFormatException ex) {}
				}
			}
		}
	}
}
