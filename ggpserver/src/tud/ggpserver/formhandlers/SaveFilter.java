package tud.ggpserver.formhandlers;

import java.sql.SQLException;

import java.util.Map;
import java.util.Set;

import tud.ggpserver.util.Filter;

import java.util.Map.Entry;

public class SaveFilter {
	private Filter filter;
	
	private int show_matches = 0;

	public int getShow_matches() {
		return show_matches;
	}


	public void setShow_matches(int showMatches) {
		show_matches = showMatches;
	}


	public void setFilter(Object filter) {
		this.filter = (Filter)filter;
	}

	
	public void parseParameterMap(Map<String, String[]> parameterMap) throws SQLException {
		Set<Entry<String,String[]>> entrySet = parameterMap.entrySet();
		for (Entry<String, String[]> entry : entrySet) {
			String key = entry.getKey();
			if (key.equals("0submit")) {
				show_matches = 1;
				continue;
			}
			
			String[] values = entry.getValue();
			filter.update(Long.valueOf(key), values);
		}

	}

	
	
}
