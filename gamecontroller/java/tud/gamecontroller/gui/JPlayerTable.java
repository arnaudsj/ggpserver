package tud.gamecontroller.gui;

import java.awt.Component;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class JPlayerTable extends JTable {

	private static final long serialVersionUID = -1024214873435431811L;

	public JPlayerTable(PlayerTableModel model) {
		super(model);
		setDefaultEditor(PlayerType.class, new PlayerTypeCellEditor());
		setDefaultRenderer(PlayerType.class, new PlayerTypeRenderer()); 
	}

	public TableCellRenderer getCellRenderer(int row, int column) {
		TableCellRenderer tcr=super.getCellRenderer(row, column);
		if(tcr instanceof DefaultTableCellRenderer){
			if(!isCellEditable(row, column)){
				((DefaultTableCellRenderer)tcr).setEnabled(false);
			}else{
				((DefaultTableCellRenderer)tcr).setEnabled(true);
			}
		}
		return tcr;
	}

	private class PlayerTypeCellEditor extends DefaultCellEditor{

		private static final long serialVersionUID = 7990970130621681632L;

		public PlayerTypeCellEditor() {
			super(new JComboBox(new PlayerType[]{PlayerType.REMOTE, PlayerType.RANDOM, PlayerType.LEGAL}));
		}
	}
	
	private class PlayerTypeRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2747455578534171093L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			return super.getTableCellRendererComponent(table, value.toString(), isSelected, hasFocus, row, column);
		}

	}
	
}
