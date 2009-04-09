package tud.ggpserver.formhandlers;

import java.sql.SQLException;

import javax.naming.NamingException;

import tud.gamecontroller.game.javaprover.Term;
import tud.ggpserver.AbstractReasonerFactory;
import tud.ggpserver.JavaProverReasonerFactory;
import tud.ggpserver.datamodel.DBConnector;
import cs227b.teamIago.util.GameState;

public abstract class AbstractPager {

	protected int startRow = 0;
	protected int numDisplayedRows = 30;
	protected final DBConnector<Term, GameState> db = new DBConnector<Term, GameState>();

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
		return db.getRowCount(getTableName());
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

	protected abstract String getTableName();
	
	protected AbstractReasonerFactory<Term, GameState> getReasonerFactory() {
		return JavaProverReasonerFactory.getInstance();
	}
}