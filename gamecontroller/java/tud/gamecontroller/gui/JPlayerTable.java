/*
    Copyright (C) 2008 Stephan Schiffel <stephan.schiffel@gmx.de>

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

	private static class PlayerTypeCellEditor extends DefaultCellEditor{

		private static final long serialVersionUID = 7990970130621681632L;

		public PlayerTypeCellEditor() {
			super(new JComboBox(new PlayerType[]{PlayerType.REMOTE, PlayerType.RANDOM, PlayerType.LEGAL}));
		}
	}
	
	private static class PlayerTypeRenderer extends DefaultTableCellRenderer {
		/**
		 * 
		 */
		private static final long serialVersionUID = -2747455578534171093L;

		public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
			return super.getTableCellRendererComponent(table, value.toString(), isSelected, hasFocus, row, column);
		}

	}
	
}
