package tud.gamecontroller.gui;

import java.awt.BorderLayout;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileFilter;

import tud.gamecontroller.game.Game;

import java.io.File;

public class GameControllerApp {

	private JFrame jFrame = null;  //  @jve:decl-index=0:visual-constraint="10,10"
	private JPanel jContentPane = null;
	private JFileChooser jFileChooser = null;
	/**
	 * This method initializes jFrame
	 * 
	 * @return javax.swing.JFrame
	 */
	private JFrame getJFrame() {
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
		}
		return jContentPane;
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
						System.out.println("loading game "+jFileChooser.getSelectedFile());
						Game game=Game.readFromFile(jFileChooser.getSelectedFile().getPath());
						GameControllerFrame frame=new GameControllerFrame(game);
						frame.setVisible(true);
					}else{
						jFrame.dispose();
					}
				}
			});
		}
		return jFileChooser;
	}

	/**
	 * Launches this application
	 */
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				GameControllerApp application = new GameControllerApp();
				application.getJFrame().setVisible(true);
			}
		});
	}

}
