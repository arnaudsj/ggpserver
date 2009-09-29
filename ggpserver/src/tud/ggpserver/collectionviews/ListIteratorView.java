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

import java.util.ListIterator;

/**
 * @author ss177134
 *
 */
public class ListIteratorView<T, T0> extends IteratorView<T, T0> implements ListIterator<T> {

	protected ListIterator<T0> listIterator;
	
	public ListIteratorView(ListIterator<T0> iterator, Mapping<T0, T> mapping) {
		super(iterator, mapping);
	}

	@Override
	public void add(T e) {
		listIterator.add(mapping.reverseMap(e));
		
	}

	@Override
	public boolean hasPrevious() {
		return listIterator.hasPrevious();
	}

	@Override
	public int nextIndex() {
		return listIterator.nextIndex();
	}

	@Override
	public T previous() {
		return mapping.map(listIterator.previous());
	}

	@Override
	public int previousIndex() {
		return listIterator.previousIndex();
	}

	@Override
	public void set(T e) {
		listIterator.set(mapping.reverseMap(e));
	}

}
