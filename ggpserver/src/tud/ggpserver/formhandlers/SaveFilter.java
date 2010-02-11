/*
    Copyright (C) 2010 Peter Steinke <peter.steinke@inf.tu-dresden.de>

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

import java.util.Map;
import java.util.Set;

import tud.ggpserver.filter.Filter;

import java.util.Map.Entry;

public class SaveFilter {
	private Filter filter;
	
	private boolean showMatches = false;

	public boolean getShowMatches() {
		return showMatches;
	}

	public void setShowMatches(boolean showMatches) {
		this.showMatches = showMatches;
	}

	public void setFilter(Object filter) {
		this.filter = (Filter)filter;
	}
	
	public void parseParameterMap(Map<String, String[]> parameterMap) throws SQLException {
		Set<Entry<String,String[]>> entrySet = parameterMap.entrySet();
		for (Entry<String, String[]> entry : entrySet) {
			String key = entry.getKey();
			if (key.equals("0submit")) {
				showMatches = true;
				continue;
			}
			
			String[] values = entry.getValue();
			filter.update(Long.valueOf(key), values);
		}

	}

	
}
