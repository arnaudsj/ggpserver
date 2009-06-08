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

/**
 * Implements a list of XML states, error messages or joint moves, backed by the
 * database. "Static" means that this list can NOT grow over time as more states /
 * error messages / joint moves are added to the match. Thus, this class must NOT be
 * used for running matches.<br>
 * 
 * The advantage of using this class is that the size() method only calls the 
 * database once to determine the number of elements. Since this is a pretty 
 * frequently-called method, this reduces the number of SQL connections drastically. 
 * 
 * @author Martin Günther <mintar@gmx.de>
 */
public class StaticDBBackedList<ElementType> extends DynamicDBBackedList<ElementType> {

	public StaticDBBackedList(DBAccessor<ElementType> accessor, boolean caching) {
		super(accessor, caching);
	}

	public StaticDBBackedList(DBAccessor<ElementType> accessor) {
		super(accessor);
	}

	@Override
	public int size() {
		if (cachedSize >= 0) {
			return cachedSize;
		}
		return super.size();
	}
}
