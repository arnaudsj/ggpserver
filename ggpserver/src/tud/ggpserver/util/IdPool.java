package tud.ggpserver.util;

import java.util.HashMap;
import java.util.Map;

public class IdPool<T> {
	private Long lastId = 0l;
	private Map<Long, T> idMap = new HashMap<Long, T>();
	
	public Long getNewId() {
		lastId++;
		if (lastId == (Long.MAX_VALUE - 1))
			lastId = 1l;
		
		return lastId;
	}
	
	public void addItem(T item, Long id) {
		idMap.put(id, item);
	}
	
	public void removeItem(Long id) {
		idMap.remove(id);
	}
	
	public T getItem(Long id) {
		return idMap.get(id);
	}
	
	public boolean containsItem(Long id) {
		return idMap.containsKey(id);
	}
}
