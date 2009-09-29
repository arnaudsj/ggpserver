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
import java.util.Iterator;
import java.util.LinkedList;

/**
 * @author ss177134
 *
 */
public class CollectionView<T, T0> implements Collection<T> {
	
	protected Collection<T0> collection;
	protected Mapping<T0, T> mapping;

	public CollectionView(Collection<T0> collection, Mapping<T0, T> mapping) {
		this.collection = collection;
		this.mapping = mapping;
	}
	
	@Override
	public boolean add(T e) {
		return collection.add(mapping.reverseMap(e));
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return collection.addAll(new CollectionView<T0, T>(new LinkedList<T>(c), new ReverseMapping<T, T0>(mapping)));
	}

	@Override
	public void clear() {
		collection.clear();
		
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean contains(Object o) {
		try {
			return collection.contains(mapping.reverseMap((T)o));
		} catch(ClassCastException e) {
			return false;
		}
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for(Object o:c) {
			if(!contains(o))
				return false;
		}
		return true;
	}

	@Override
	public boolean isEmpty() {
		return collection.isEmpty();
	}

	@Override
	public Iterator<T> iterator() {
		return new IteratorView<T, T0>(collection.iterator(), mapping);
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean remove(Object o) {
		return collection.remove(mapping.reverseMap((T)o));
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean removeAll(Collection<?> c) {
		return collection.removeAll(new CollectionView<T0, T>((Collection<T>)c, new ReverseMapping<T, T0>(mapping)));
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean retainAll(Collection<?> c) {
		return collection.retainAll(new CollectionView<T0, T>((Collection<T>)c, new ReverseMapping<T, T0>(mapping)));
	}

	@Override
	public int size() {
		return collection.size();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Object[] toArray() {
		T0[] t0Array = (T0[])collection.toArray();
		T[] tArray = (T[])new Object[t0Array.length];
		for(int i = 0; i<t0Array.length; i++) {
			tArray[i] = mapping.map(t0Array[i]);
		}
		return tArray;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T1> T1[] toArray(T1[] a) {
		// TODO: clean implementation of this method
		return (T1[])toArray();
	}

}
