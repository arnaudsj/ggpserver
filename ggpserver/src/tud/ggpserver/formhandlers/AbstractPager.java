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

package tud.ggpserver.formhandlers;

import java.sql.SQLException;

import tud.ggpserver.datamodel.DBConnectorFactory;

public abstract class AbstractPager {
	protected int startRow = 0;
	protected int numDisplayedRows = 30;
	
	public AbstractPager() {
		super();
	}
	
	public int getNumDisplayedRows() {
		return numDisplayedRows;
	}

	public void setNumDisplayedRows(int numDisplayedRows) {
		this.numDisplayedRows = numDisplayedRows;
	}

	public int getStartRow() {
		return startRow;
	}

	public void setStartRow(int startRow) {
		this.startRow = startRow;
	}

	public int getEndRow() throws SQLException {
		int numReallyDisplayedRows;
		
		if (getPage() == getNumberOfPages()) {   // last page
			numReallyDisplayedRows = getRowCount() - (getNumberOfPages() - 1) * numDisplayedRows;
		} else {   // not last page
			numReallyDisplayedRows = numDisplayedRows;
		}
		return startRow + numReallyDisplayedRows - 1;
		
	}

	public int getNumberOfPages() throws SQLException {
		return (getRowCount() - 1) / numDisplayedRows + 1;
		
	}

	protected int getRowCount() throws SQLException {
		return DBConnectorFactory.getDBConnector().getRowCount(getTableName());
	}

	public int getPage() {
		return (startRow / numDisplayedRows) + 1;
	}

	/**
	 * starts with 1
	 */
	public void setPage(int page) throws SQLException {
		if (page < 0 || page > getNumberOfPages()) {
			throw new IllegalArgumentException("Page negative or > number of pages.");
		}
		startRow = (page - 1) * numDisplayedRows;
	}

	public abstract String getTargetJsp();

	public abstract String getTableName();
}