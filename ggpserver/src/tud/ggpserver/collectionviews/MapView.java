/*
    Copyright (C) 2009 Stephan Schiffel <stephan.schiffel@gmx.de> 

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

package tud.ggpserver.collectionviews;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * @author ss177134
 *
 */
public class MapView<K, V, K0, V0> implements Map<K, V> {
	
	private Map<K0, V0> map;
	private Mapping<K0, K> keyMapping;
	private Mapping<V0, V> valueMapping;
	
	public MapView(Map<K0, V0> map, Mapping<K0, K> keyMapping, Mapping<V0, V> valueMapping) {
		this.map = map;
	}

	@Override
	public void clear() {
		map.clear();
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean containsKey(Object key) {
		return map.containsKey(keyMapping.reverseMap((K)key));
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean containsValue(Object value) {
		return map.containsValue(valueMapping.reverseMap((V)value));
	}

	@Override
	public Set<Entry<K, V>> entrySet() {
		return new SetView<Entry<K,V>, Entry<K0,V0>>(map.entrySet(), new EntryMapping());
	}

	@SuppressWarnings("unchecked")
	@Override
	public V get(Object key) {
		return valueMapping.map(map.get(keyMapping.reverseMap((K)key)));
	}

	@Override
	public boolean isEmpty() {
		return map.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return new SetView<K, K0>(map.keySet(), keyMapping);
	}

	@Override
	public V put(K key, V value) {
		return valueMapping.map(map.put(keyMapping.reverseMap(key), valueMapping.reverseMap(value)));
	}

	@SuppressWarnings("unchecked")
	@Override
	public void putAll(Map<? extends K, ? extends V> m) {
		map.putAll(new MapView<K0, V0, K, V>((Map<K,V>)m, new ReverseMapping<K, K0>(keyMapping), new ReverseMapping<V, V0>(valueMapping)));
	}

	@SuppressWarnings("unchecked")
	@Override
	public V remove(Object key) {
		return valueMapping.map(map.remove(keyMapping.reverseMap((K)key)));
	}

	@Override
	public int size() {
		return map.size();
	}

	@Override
	public Collection<V> values() {
		return new CollectionView<V, V0>(map.values(), valueMapping);
	}

	private class EntryMapping implements Mapping<Entry<K0, V0>, Entry<K, V>> {
		
		public Entry<K, V> map(final Entry<K0, V0> o) {
			return new Entry<K, V>(){

				public K getKey() {
					return keyMapping.map(o.getKey());
				}

				@Override
				public V getValue() {
					return valueMapping.map(o.getValue());
				}

				@Override
				public V setValue(V value) {
					return valueMapping.map(o.setValue(valueMapping.reverseMap(value)));
				}
			};
		}

		public Entry<K0, V0> reverseMap(final Entry<K, V> o) {
			return new Entry<K0, V0>(){

				public K0 getKey() {
					return keyMapping.reverseMap(o.getKey());
				}

				@Override
				public V0 getValue() {
					return valueMapping.reverseMap(o.getValue());
				}

				@Override
				public V0 setValue(V0 value) {
					return valueMapping.reverseMap(o.setValue(valueMapping.map(value)));
				}
			};
		}
	}
}
