package tud.ggpserver.formhandlers;

import java.sql.SQLException;

import javax.naming.NamingException;

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

	public int getEndRow() throws NamingException, SQLException {
		int numReallyDisplayedRows;
		
		if (getPage() == getNumberOfPages()) {   // last page
			numReallyDisplayedRows = getRowCount() - (getNumberOfPages() - 1) * numDisplayedRows;
		} else {   // not last page
			numReallyDisplayedRows = numDisplayedRows;
		}
		return startRow + numReallyDisplayedRows - 1;
		
	}

	public int getNumberOfPages() throws NamingException, SQLException {
		return (getRowCount() - 1) / numDisplayedRows + 1;
		
	}

	protected int getRowCount() throws NamingException, SQLException {
		return DBConnectorFactory.getDBConnector().getRowCount(getTableName());
	}

	public int getPage() {
		return (startRow / numDisplayedRows) + 1;
	}

	/**
	 * starts with 1
	 * @throws SQLException 
	 * @throws NamingException 
	 */
	public void setPage(int page) throws NamingException, SQLException {
		if (page < 0 || page > getNumberOfPages()) {
			throw new IllegalArgumentException("Page negative or > number of pages.");
		}
		startRow = (page - 1) * numDisplayedRows;
	}

	public abstract String getTargetJsp();

	public abstract String getTableName();
}