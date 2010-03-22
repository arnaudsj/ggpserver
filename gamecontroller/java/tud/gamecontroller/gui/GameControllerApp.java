/*
    Copyright (C) 2008-2010 Stephan Schiffel <stephan.schiffel@gmx.de>
                  2010 Nicolas JEAN <njean42@gmail.com>

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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.io.File;

import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import tud.gamecontroller.GDLVersion;

public class GameControllerApp {

	private JFrame jFrame = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private JPanel jContentPane = null;
	private JFileChooser jFileChooser = null;
	
	// choose the GDL version
	private JComboBox jGDLVersionComboBox = null;
	
	public GameControllerApp(){
	}
	
	/**
	 * This method initializes jFrame
	 * 
	 * @return javax.swing.JFrame
	 */
	protected JFrame getJFrame() {
		if (jFrame == null) {
			jFrame = new JFrame();
			jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			jFrame.setSize(607, 428);
			jFrame.setContentPane(getJContentPane());
			jFrame.setTitle("Start game ...");
		}
		return jFrame;
	}

	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new JPanel();
			jContentPane.setLayout(new BorderLayout());
			jContentPane.add(getJFileChooser(), BorderLayout.CENTER);
			jContentPane.add(getJGDLVersion(), BorderLayout.SOUTH);
		}
		return jContentPane;
	}

	private Component getJGDLVersion() {
		
		if (jGDLVersionComboBox == null) {
			jGDLVersionComboBox = new JComboBox(new String[]{"Regular GDL", "GDL-II"});
			jGDLVersionComboBox.setEditable(false);
			jGDLVersionComboBox.setPreferredSize(new Dimension(80, 24));
		}
		return jGDLVersionComboBox;
		
	}

	private JFileChooser getJFileChooser() {
		if (jFileChooser == null) {
			jFileChooser = new JFileChooser();
			FileFilter filter = new FileFilter(){
				public boolean accept(File f) {
					return f.getName().endsWith(".gdl") || f.getName().endsWith(".kif") || f.getName().endsWith(".lisp") || f.isDirectory(); 
				}
				public String getDescription() {
					return "Game Description Language files (*.gdl, *.kif, *.lisp)";
				}
				
			};
			jFileChooser.addChoosableFileFilter(filter);
			jFileChooser.setFileFilter(filter);
			jFileChooser.setApproveButtonText("Start Game");
			jFileChooser.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent event) {
					if(event.getActionCommand().equals(JFileChooser.APPROVE_SELECTION)){
						
						GDLVersion gdlVersion = GDLVersion.v1;
						if ( "GDL-II".equals(jGDLVersionComboBox.getSelectedItem()) ) {
							gdlVersion = GDLVersion.v2;
						}
						
						AbstractGameControllerGuiRunner<?, ?> gameControllerRunner = GameControllerGuiRunnerFactory.createGameControllerGuiRunner(jFileChooser.getSelectedFile());
						gameControllerRunner.setGdlVersion(gdlVersion);
						gameControllerRunner.runGui();
					}else{
						jFrame.dispose();
					}
				}
			});
		}
		return jFileChooser;
	}
	
	public static void main(String[] args){
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GameControllerApp application = new GameControllerApp();
				application.getJFrame().setVisible(true);
			}
		});
	}
}
