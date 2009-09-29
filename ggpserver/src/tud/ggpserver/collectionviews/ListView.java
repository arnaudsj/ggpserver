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
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class ListView<T, T0> extends CollectionView<T, T0> implements List<T> {

	private List<T0> list;
	
	public ListView(List<T0> list, Mapping<T0, T> mapping) {
		super(list, mapping);
		this.list = list;
	}

	@Override
	public void add(int index, T element) {
		list.add(index, mapping.reverseMap(element));
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
		return list.addAll(index, new CollectionView<T0, T>(new LinkedList<T>(c), new ReverseMapping<T, T0>(mapping)));
	}

	@Override
	public T get(int index) {
		return mapping.map(list.get(index));
	}

	@SuppressWarnings("unchecked")
	@Override
	public int indexOf(Object o) {
		return list.indexOf(mapping.reverseMap((T)o));
	}

	@SuppressWarnings("unchecked")
	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(mapping.reverseMap((T)o));
	}

	@Override
	public ListIterator<T> listIterator() {
		return new ListIteratorView<T, T0>(list.listIterator(), mapping);
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return new ListIteratorView<T, T0>(list.listIterator(index), mapping);
	}

	@Override
	public T remove(int index) {
		return mapping.map(list.remove(index));
	}

	@Override
	public T set(int index, T element) {
		return mapping.map(list.set(index, mapping.reverseMap(element)));
	}

	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return new ListView<T, T0>(list.subList(fromIndex, toIndex), mapping);
	}

}
