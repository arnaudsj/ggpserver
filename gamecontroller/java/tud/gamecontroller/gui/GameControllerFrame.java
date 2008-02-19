package tud.gamecontroller.gui;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.ConsoleHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.text.JTextComponent;

import tud.gamecontroller.GameController;
import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.javaprover.Game;
import tud.gamecontroller.game.javaprover.State;
import tud.gamecontroller.game.javaprover.Term;
import tud.gamecontroller.game.javaprover.TermFactory;
import tud.gamecontroller.gui.PlayerTableModel.PlayerRecord;
import tud.gamecontroller.logging.PlainTextLogFormatter;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.players.PlayerInfo;

public class GameControllerFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	private JPanel jContentPane = null;
	
	private Game game;

	private JPanel jClockPanel = null;

	private JPanel jButtonsPanel = null;

	private JButton jStartGameButton = null;

	private JButton jStopGameButton = null;

	private JButton jExitButton = null;

	private JTextArea jLogPane = null;

	private JLabel jStartclockLabel = null;

	private JLabel jPlayclockLabel = null;

	private JComboBox jStartclockComboBox = null;

	private JComboBox jPlayclockComboBox = null;

	private JTable jPlayersTable = null;

	private GameController<Term,State> gameController = null;  //  @jve:decl-index=0:

	private PlayerTableModel playerTableModel = null;

	private Thread gameThread;  //  @jve:decl-index=0:

	private JScrollPane jLogScrollPane = null;

	private JScrollPane jTableScrollPane = null;
	/**
	 * This is the default constructor
	 * @param game 
	 */
	public GameControllerFrame(Game game) {
		super();
		this.game=game;
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		this.setSize(559, 422);
		this.setContentPane(getJContentPane());
		this.setTitle("GameController ("+game.getName()+")");
	}

	private void close() {
		this.dispose();
	}
	
	/**
	 * This method initializes jContentPane
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getJContentPane() {
		if (jContentPane == null) {
			GridBagConstraints gridBagConstraints6 = new GridBagConstraints();
			gridBagConstraints6.fill = GridBagConstraints.BOTH;
			gridBagConstraints6.weightx = 1.0;
			gridBagConstraints6.weighty = 1.0;
			gridBagConstraints6.gridx = 0;
			gridBagConstraints6.gridy = 0;
			GridBagConstraints gridBagConstraints5 = new GridBagConstraints();
			gridBagConstraints5.fill = GridBagConstraints.BOTH;
			gridBagConstraints5.weightx = 1.0;
			gridBagConstraints5.weighty = 1.0;
			gridBagConstraints5.gridx = 0;
			gridBagConstraints5.gridy = 1;
			GridBagConstraints gridBagConstraints4 = new GridBagConstraints();
			gridBagConstraints4.fill = GridBagConstraints.BOTH;
			gridBagConstraints4.weightx = 1.0;
			gridBagConstraints4.weighty = 1.0;
			gridBagConstraints4.gridx = 0;
			gridBagConstraints4.gridy = 2;
			GridBagConstraints gridBagConstraints8 = new GridBagConstraints();
			gridBagConstraints8.fill = GridBagConstraints.BOTH;
			gridBagConstraints8.weightx = 1.0;
			gridBagConstraints8.weighty = 5.0;
			gridBagConstraints8.gridx = 0;
			gridBagConstraints8.gridy = 3;
			jContentPane = new JPanel();
			jContentPane.setLayout(new GridBagLayout());
			jContentPane.add(getJClockPanel(), gridBagConstraints6);
			jContentPane.add(getJTableScrollPane(), gridBagConstraints5);
			jContentPane.add(getJButtonsPanel(), gridBagConstraints4);
			jContentPane.add(getJLogScrollPane(), gridBagConstraints8);
		}
		return jContentPane;
	}

	/**
	 * This method initializes jClockPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJClockPanel() {
		if (jClockPanel == null) {
			GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
			gridBagConstraints3.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints3.gridy = 1;
			gridBagConstraints3.weightx = 1.0;
			gridBagConstraints3.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints3.anchor = GridBagConstraints.WEST;
			gridBagConstraints3.gridx = 1;
			GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
			gridBagConstraints2.gridx = 0;
			gridBagConstraints2.weightx = 1.0;
			gridBagConstraints2.anchor = GridBagConstraints.EAST;
			gridBagConstraints2.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints2.gridy = 1;
			GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
			gridBagConstraints1.fill = GridBagConstraints.VERTICAL;
			gridBagConstraints1.gridx = 1;
			gridBagConstraints1.gridy = 0;
			gridBagConstraints1.weightx = 1.0;
			gridBagConstraints1.anchor = GridBagConstraints.WEST;
			gridBagConstraints1.insets = new Insets(2, 2, 2, 2);
			GridBagConstraints gridBagConstraints = new GridBagConstraints();
			gridBagConstraints.insets = new Insets(2, 2, 2, 2);
			gridBagConstraints.gridy = 0;
			gridBagConstraints.anchor = GridBagConstraints.EAST;
			gridBagConstraints.gridx = 0;
			gridBagConstraints.weightx = 1.0;
			jStartclockLabel = new JLabel();
			jStartclockLabel.setText("Startclock");
			jPlayclockLabel = new JLabel();
			jPlayclockLabel.setText("Playclock");
			jClockPanel = new JPanel();
			jClockPanel.setLayout(new GridBagLayout());
			jClockPanel.add(jStartclockLabel, gridBagConstraints);
			jClockPanel.add(getJStartclockComboBox(), gridBagConstraints1);
			jClockPanel.add(jPlayclockLabel, gridBagConstraints2);
			jClockPanel.add(getJPlayclockComboBox(), gridBagConstraints3);
		}
		return jClockPanel;
	}

	/**
	 * This method initializes jButtonsPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getJButtonsPanel() {
		if (jButtonsPanel == null) {
			jButtonsPanel = new JPanel();
			jButtonsPanel.setLayout(new FlowLayout());
			jButtonsPanel.add(getJStartGameButton(), null);
			jButtonsPanel.add(getJStopGameButton(), null);
			jButtonsPanel.add(getJExitButton(), null);
		}
		return jButtonsPanel;
	}

	/**
	 * This method initializes jStartGameButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJStartGameButton() {
		if (jStartGameButton == null) {
			jStartGameButton = new JButton();
			jStartGameButton.setText("Start");
			jStartGameButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jStartGameButton.setEnabled(false);
					startGame();
					jStopGameButton.setEnabled(true);
				}
			});
		}
		return jStartGameButton;
	}

	private void startGame() {
		int startclock, playclock;
		startclock=((Integer)jStartclockComboBox.getSelectedItem()).intValue();
		playclock=((Integer)jPlayclockComboBox.getSelectedItem()).intValue();
		Collection<PlayerInfo> players=new LinkedList<PlayerInfo>();
		for(PlayerRecord p:playerTableModel.getPlayerRecords()){
			players.add(p.getPlayerInfo());
		}
		Logger logger=Logger.getAnonymousLogger();
		logger.addHandler(new ConsoleHandler());
		Handler jLogPaneAppender=new Handler(){
			public void close() throws SecurityException {
			}
			public void flush() {
			}
			public void publish(LogRecord record) {
				jLogPane.append(getFormatter().format(record)+"\n");
			}
		};
		jLogPaneAppender.setFormatter(new PlainTextLogFormatter());
		jLogPaneAppender.setLevel(Level.ALL);
		logger.addHandler(jLogPaneAppender);
		gameController=new GameController<Term,State>(new Match<Term,State,Player<Term,State>>("testmatch", game, startclock, playclock, null), players, new TermFactory(), null, logger);
		gameThread=new Thread(){
			public void run(){
				gameController.runGame();
				List<Integer> goalValues=gameController.getGoalValues();
				for(int i=0;i<goalValues.size();i++){
					playerTableModel.getPlayerRecords()[i].setValue(goalValues.get(i));
				}
				jStopGameButton.setEnabled(false);
				gameThread=null;
				gameController=null;
				jStartGameButton.setEnabled(true);
			}
		};
		gameThread.start();
	}

	private void stopGame() {
		gameThread.interrupt();
		gameThread=null;
		gameController=null;
	}

	/**
	 * This method initializes jStopGameButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJStopGameButton() {
		if (jStopGameButton == null) {
			jStopGameButton = new JButton();
			jStopGameButton.setText("Stop");
			jStopGameButton.setEnabled(false);
			jStopGameButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					jStopGameButton.setEnabled(false);
					stopGame();
					jStartGameButton.setEnabled(true);
				}
			});
		}
		return jStopGameButton;
	}

	/**
	 * This method initializes jExitButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getJExitButton() {
		if (jExitButton == null) {
			jExitButton = new JButton();
			jExitButton.setText("Exit");
			jExitButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					close();
				}
			});
		}
		return jExitButton;
	}

	/**
	 * This method initializes jLogPane	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	private JTextComponent getJLogPane() {
		if (jLogPane == null) {
			jLogPane = new JTextArea();
			jLogPane.setEditable(false);
			jLogPane.setWrapStyleWord(true);
			jLogPane.setLineWrap(true);
		}
		return jLogPane;
	}

	/**
	 * This method initializes jStartclockComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJStartclockComboBox() {
		if (jStartclockComboBox == null) {
			jStartclockComboBox = new JComboBox(new Integer[]{10,30,60,120,180,300,600,900,1200,1800,3600});
			jStartclockComboBox.setEditable(true);
			jStartclockComboBox.setPreferredSize(new Dimension(80, 24));
		}
		return jStartclockComboBox;
	}

	/**
	 * This method initializes jPlayclockComboBox	
	 * 	
	 * @return javax.swing.JComboBox	
	 */
	private JComboBox getJPlayclockComboBox() {
		if (jPlayclockComboBox == null) {
			jPlayclockComboBox = new JComboBox(new Integer[]{5,10,30,60,120,180,300});
			jPlayclockComboBox.setEditable(true);
			jPlayclockComboBox.setPreferredSize(new Dimension(80, 24));
		}
		return jPlayclockComboBox;
	}

	/**
	 * This method initializes jPlayersTable	
	 * 	
	 * @return javax.swing.JTable	
	 */
	private JTable getJPlayersTable() {
		if (jPlayersTable == null) {
			playerTableModel =new PlayerTableModel(game);
			jPlayersTable = new JPlayerTable(playerTableModel);
			jPlayersTable.setShowGrid(true);
			jPlayersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		}
		return jPlayersTable;
	}

	/**
	 * This method initializes jLogScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJLogScrollPane() {
		if (jLogScrollPane == null) {
			jLogScrollPane = new JScrollPane();
			jLogScrollPane.setViewportView(getJLogPane());
		}
		return jLogScrollPane;
	}

	/**
	 * This method initializes jTableScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getJTableScrollPane() {
		if (jTableScrollPane == null) {
			jTableScrollPane = new JScrollPane();
			jTableScrollPane.setViewportView(getJPlayersTable());
		}
		return jTableScrollPane;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
