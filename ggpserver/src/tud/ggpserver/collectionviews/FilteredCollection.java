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

import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * A view of a Collection that only shows elements that fulfill certain criteria 
 *
 */
public class FilteredCollection<T> extends AbstractCollection<T> {
	
	public interface Filter<T> {

		boolean accept(T element);
		
	}

	private Collection<T> decorated;
	private int size = -1;
	private Filter<T> filter;

	public FilteredCollection(Collection<T> decorated, Filter<T> filter) {
		this.decorated = decorated;
		this.filter = filter;
	}

	@Override
	public Iterator<T> iterator() {
		return new FilteredIterator(decorated.iterator());
	}

	/**
	 * Avoid calling this method if possible, it iterates over the whole Collection.
	 * @return the number of elements in the collection
	 */
	@Override
	public int size() {
		if ( size == -1 ) {
			size=0;
			Iterator<T> it = iterator();
			while(it.hasNext()) {
				it.next();
				++size;
			}
		}
		return size;
	}

	private class FilteredIterator implements Iterator<T> {

		private Iterator<T> decoratedIt;
		private T nextElement;
		private boolean hasNextElement;
		
		public FilteredIterator(Iterator<T> decoratedIt) {
			this.decoratedIt = decoratedIt;
			findNextElement();
		}

		private void findNextElement() {
			hasNextElement = false;
			nextElement = null;
			while( decoratedIt.hasNext() && !hasNextElement) {
				nextElement = decoratedIt.next();
				if ( filter.accept(nextElement) ) {
					hasNextElement = true;
				}
			}
		}
		
		@Override
		public boolean hasNext() {
			return hasNextElement;
		}

		@Override
		public T next() {
			T element;
			if ( hasNextElement ) {
				element = nextElement;
				findNextElement();
			} else {
				throw new NoSuchElementException();
			}
			return element;
		}

		@Override
		public void remove() {
			throw new UnsupportedOperationException();
		}
		
	}
}
