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

package tud.ggpserver.util;

import java.util.HashMap;
import java.util.Map;

/**
 * implements a map that associates unique IDs to arbitrary items
 *
 * @param <T>
 */
public class IdPool<T extends IDItem> {
	private long lastId = 0l;
	private Map<Long, T> idMap = new HashMap<Long, T>();
	
	public Long getNewId() {
		if (lastId == Long.MAX_VALUE) {
			throw new RuntimeException("Ran out of unique ids in IdPool.getNewId()!");
		}
		++lastId;
		return lastId;
	}

	public long getNewId(T item) {
		long id = getNewId();
		addItem(item, id);
		item.setId(id);
		return id;
	}

	private void addItem(T item, long id) {
		idMap.put(id, item);
	}
	
	public void removeItem(long id) {
		idMap.remove(id);
	}
	
	public T getItem(long id) {
		return idMap.get(id);
	}
	
	public boolean containsItem(long id) {
		return idMap.containsKey(id);
	}
}
