/*
 Copyright (C) 2009 Martin Günther <mintar@gmx.de> 

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

package tud.ggpserver.datamodel.dblists;

import java.sql.SQLException;
import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Implements a list of XML states, error messages or joint moves, backed by the
 * database. "Dynamic" means that this list can grow over time as more states /
 * error messages / joint moves are added to the match. Thus, this class can be
 * used for running matches.<br>
 * 
 * The flip side of this dynamic behaviour is that it will result in more SQL
 * connections.
 * 
 * @author Martin Günther <mintar@gmx.de>
 */
public class DynamicDBBackedList<ElementType> extends AbstractList<ElementType> {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(DynamicDBBackedList.class.getName());

	private final DBAccessor<ElementType> accessor;
	
	/**
	 * Caching on / off. 
	 */
	private final boolean caching;
	
	/**
	 * The number of elements read so far. Since no elements can be removed in
	 * the database, but only added, the actual number of elements in the
	 * database is always as least as big as <code>cachedSize</code>.<br>
	 * 
	 * The purpose of this is to minimize the number of sql connections through
	 * calls to accessor.getSize(). <code>cachedSize</code> will always be used,
	 * independent from the value of <code>caching</code>.
	 */
	int cachedSize = -1;

	private List<ElementType> cache;
	
	public DynamicDBBackedList(final DBAccessor<ElementType> accessor, final boolean caching) {
		this.accessor = accessor;
		this.caching = caching;
		if (caching) {
			// fill the cache in one sweep (only one sql connection needed to fill all elements)
			try {
				cache = new ArrayList<ElementType>(accessor.getAllElements());
			} catch (SQLException e) {
				logger.severe("DBAccessor<ElementType>, boolean - exception: " + e); //$NON-NLS-1$
				cache = new ArrayList<ElementType>();
			}
		}
	}
	
	public DynamicDBBackedList(final DBAccessor<ElementType> accessor) {
		this(accessor, false);
	}
	

	@Override
	public ElementType get(int stepNumber) {
		if (stepNumber < 0) {
			throw new IndexOutOfBoundsException();
		}
		if (stepNumber >= cachedSize) {
			// re-read size and check again
			refreshCachedSize();
			if (stepNumber >= cachedSize) {
				throw new IndexOutOfBoundsException();
			}
		}
		
		try {
			if (caching) {
				return getCachedElement(stepNumber);
			}
			
			return accessor.getElement(stepNumber);
		} catch (SQLException e) {
			IndexOutOfBoundsException e2 = new IndexOutOfBoundsException(e.getMessage());
			e2.initCause(e);
			throw e2;
		}
	}

	@Override
	public int size() {
		try {
			return accessor.getSize();
		} catch (SQLException e) {
			InternalError internalError = new InternalError(e.getMessage());
			internalError.initCause(e);
			throw internalError;
		}
	}

	private ElementType getCachedElement(int stepNumber) throws SQLException {
		ElementType result = cache.get(stepNumber);
		if (result == null) {
			result = accessor.getElement(stepNumber);
			cache.set(stepNumber, result);
		}
		return result;
	}

	private void refreshCachedSize() {
		cachedSize = size();
		if (caching) {
			growCache(cachedSize);
		}
	}

	/**
	 * Grows the cache so it can hold at least <code>size</code> elements.
	 */
	private void growCache(int size) {
		while (cache.size() < size) {
			cache.add(null);
		}
	}
		
}
