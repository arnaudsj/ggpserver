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
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

/**
 * CrossProduct computes a cross product of a collection of lists on the fly.
 * It contains a list of tuples with each combination of elements of given lists. 
 *  
 * @author "Stephan Schiffel" <stephan.schiffel@gmx.de>
 *
 * @param <E> the type of the elements of the tuples, so far all elements must be of the same type
 */
public class CrossProduct<E> extends AbstractCollection<List<E>> implements
		Collection<List<E>> {

	private final class CrossProductIterator implements
			Iterator<List<E>> {
		
		private List<Iterator<E>> iterators;
		private List<E> nextTuple;
		private int tupleSize;
		
		public CrossProductIterator(List<? extends Iterable<E>> iterables) {
			super();
			tupleSize = iterables.size();
			iterators = new Vector<Iterator<E>>(tupleSize);
			nextTuple = new Vector<E>(tupleSize);
			for (Iterable<E> iterable : iterables) {
				Iterator<E> iterator = iterable.iterator(); 
				iterators.add(iterator);
				if(nextTuple!=null && iterator.hasNext()) {
					nextTuple.add(iterator.next());
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
		public List<E> next() {
			List<E> currentTuple = nextTuple;
			if(currentTuple!=null) {
				nextTuple = new Vector<E>(currentTuple);
				int i=0;
				while(i<tupleSize && !iterators.get(i).hasNext()) {
					Iterator<E> iterator = collections.get(i).iterator(); 
					iterators.set(i, iterator);
					nextTuple.set(i, iterator.next());
					++i;
				}
				if(i<=tupleSize){
					nextTuple.set(i, iterators.get(i).next());
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

	private List<Collection<E>> collections;
	
	public CrossProduct(List<Collection<E>> collections) {
		super();
		this.collections = collections;
	}

	@Override
	public Iterator<List<E>> iterator() {
		return new CrossProductIterator(collections);
	}

	@Override
	public int size() {
		int size = 1;
		for (Collection<E> collection : collections) {
			size*=collection.size();
		}
		return size;
	}


}
