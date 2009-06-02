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
import java.util.AbstractList;
import java.util.LinkedList;
import java.util.List;

import tud.ggpserver.datamodel.DBConnectorFactory;

public abstract class AbstractPager {
	protected int startRow = 0;
	protected int numDisplayedRows = 30;
	protected int maxNumDisplayedLinks = 21; // numbers with maxNumDisplayedLinks % 4 == 1 work best here
	
	public AbstractPager() {
		super();
	}
	
	public int getNumDisplayedRows() {
		return numDisplayedRows;
	}

	public void setNumDisplayedRows(int numDisplayedRows) {
		this.numDisplayedRows = numDisplayedRows;
	}

	public void setMaxNumDisplayedLinks(int maxNumDisplayedLinks) {
		this.maxNumDisplayedLinks = maxNumDisplayedLinks;
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

	public List<Integer> getLinkedPages() throws SQLException {
		final int numPages=getNumberOfPages();
		
		if(numPages <= maxNumDisplayedLinks){
			// show links to all pages
			return new AbstractList<Integer>(){
				@Override
				public Integer get(int index) {
					return index+1;
				}
				@Override
				public int size() {
					return numPages;
				}
			};
		}
		
		// Show links to pages
		//		1 to n/4,
		//		getPage()-n/4 to getPage()+n/4,
		//		numPages-n/4 to numPages,
		// where n is the maximum number of displayed links.
		// That means we use 1/4th of the links for pages 1,2,...,
		// half of the links for pages around the current one and
		// 1/4th of the links for the last pages.
		// If the regions overlap we move the middle part in the other direction so we always get exactly maxNumDisplayedLinks. 
		LinkedList<Integer> result=new LinkedList<Integer>();
		
		int lastPageStart = (maxNumDisplayedLinks - 1) / 4;
		int firstPageEnd = numPages + 1 - maxNumDisplayedLinks / 4;
		int numMiddlePages = maxNumDisplayedLinks - lastPageStart - (numPages + 1 - firstPageEnd); 
		int firstPageMiddle = getPage() - (numMiddlePages - 1) / 2;
		int lastPageMiddle = firstPageMiddle + numMiddlePages - 1;
		if(lastPageStart >= firstPageMiddle){
			// move the middle part to the right
			lastPageMiddle += lastPageStart - firstPageMiddle + 1;	
			firstPageMiddle = lastPageStart + 1;
		}
		if(firstPageEnd <= lastPageMiddle){
			// move the middle part to the left
			firstPageMiddle -= lastPageMiddle - firstPageEnd + 1;
			lastPageMiddle = firstPageEnd - 1;
			if(firstPageMiddle <= lastPageStart) {
				// actually this shouldn't happen because it means that numPages>maxNumDisplayedLinks
				firstPageMiddle = lastPageStart + 1;
			}
		}
		int i;
		for(i=1; i<=lastPageStart; i++){
			result.add(i);	
		}
		for(i=firstPageMiddle; i<=lastPageMiddle; i++){
			result.add(i);	
		}
		for(i=firstPageEnd; i<=numPages; i++){
			result.add(i);	
		}
		return result;
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

	/**
	 * This is the URL of the page that the result pages will link to (without the "page" parameter).
	 */
	public abstract String getTargetJsp();

	/**
	 * This String is used for two things:<br>
	 * 1. The name to be used in the pager title (see inc/pager_title.jsp)<br>
	 * 2. The database table to be used for calculating the row count. If a
	 *    subclass doesn't simply display all entries from one table, a more
	 *    complex query must be used for calculating the row count. This can be
	 *    achieved by overriding getRowCount().
	 */
	public abstract String getTableName();
}