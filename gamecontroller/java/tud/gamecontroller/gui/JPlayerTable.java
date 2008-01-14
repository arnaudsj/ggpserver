package tud.gamecontroller.gui;

import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

public class JPlayerTable extends JTable {

	private static final long serialVersionUID = -1024214873435431811L;

	public JPlayerTable(PlayerTableModel model) {
		super(model);
		setDefaultEditor(PlayerTableModel.PlayerType.class, new PlayerTypeCellEditor());
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
			super(new JComboBox(new PlayerTableModel.PlayerType[]{PlayerTableModel.PlayerType.REMOTE, PlayerTableModel.PlayerType.RANDOM, PlayerTableModel.PlayerType.LEGAL}));
		}
	}
}
