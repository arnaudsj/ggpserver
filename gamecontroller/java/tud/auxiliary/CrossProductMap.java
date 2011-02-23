/*
    Copyright (C) 2011 Stephan Schiffel <stephan.schiffel@gmx.de>

    This file is part of GameController.

    GameController is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GameController is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GameController.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.auxiliary;

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.Map;

/**
 * CrossProductMap computes a cross product of a HashMap of collections on the fly.
 * It contains a list of HashMaps with each combination of elements of the given lists, e.g.,
 * if you put in {a -> [1, 2], b -> [3, 4]} you will get [{a->1, b->3}, {a->2, b->3}, {a->1, b->4}, {a->2, b->4}]. 
 *  
 * @author "Stephan Schiffel" <stephan.schiffel@gmx.de>
 *
 * @param <E> the type of the elements of the tuples, so far all elements must be of the same type
 */
public class CrossProductMap<K, V> extends AbstractCollection<Map<K, V>> implements Collection<Map<K, V>> {

	private Map<K, Collection<? extends V>> collections;

	public CrossProductMap(Map<K, Collection<? extends V>> collections) {
		super();
		this.collections = collections;
	}

	@Override
	public Iterator<Map<K, V>> iterator() {
		return new CrossProductMapIterator();
	}

	@Override
	public int size() {
		int size = 1;
		for (Collection<? extends V> collection : collections.values()) {
			size*=collection.size();
		}
		return size;
	}


	private final class CrossProductMapIterator implements
			Iterator<Map<K, V>> {

		private Map<K, Iterator<? extends V>> iterators;
		private Map<K, V> nextTuple;
		private List<K> keys;

		public CrossProductMapIterator() {
			super();
			iterators = new HashMap<K, Iterator<? extends V>>();
			nextTuple = new HashMap<K, V>();
			keys = new Vector<K>(collections.keySet());
			for (K key:keys) {
				Iterator<? extends V> iterator = collections.get(key).iterator(); 
				iterators.put(key, iterator);
				if(nextTuple!=null && iterator.hasNext()) {
					nextTuple.put(key, iterator.next());
				} else {
					nextTuple=null;
				}
			}
		}

		@Override
		public boolean hasNext() {
			return nextTuple!=null;
		}

		@Override
		public Map<K,V> next() {
			Map<K, V> currentTuple = nextTuple;
			if(currentTuple!=null) {
				nextTuple = new HashMap<K,V>(currentTuple); // copy the tuple
				Iterator<K> keyIterator = keys.iterator();
				K key = null;
				while(keyIterator.hasNext() && (key = keyIterator.next()) != null && !iterators.get(key).hasNext()) {
					// reset this iterator to the start and put first element in currentTuple  
					Iterator<? extends V> iterator = collections.get(key).iterator(); 
					iterators.put(key, iterator);
					nextTuple.put(key, iterator.next());
					key = null;
				}
				if(key != null){
					nextTuple.put(key, iterators.get(key).next());
				} else {
					nextTuple=null;
				}
			}
			return currentTuple;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
	}


}
