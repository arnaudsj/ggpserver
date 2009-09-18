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

package tud.ggpserver.scheduler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.ConfigOption;
import tud.ggpserver.datamodel.Game;
import tud.ggpserver.datamodel.matches.NewMatch;

public abstract class AbstractRoundRobinScheduler<TermType extends TermInterface, ReasonerStateInfoType> {
	public static final String ROUND_ROBIN_TOURNAMENT_ID = "round_robin_tournament";
	private static final Logger logger = Logger.getLogger(AbstractRoundRobinScheduler.class.getName());
	private static final Random random = new Random();
	private static final long DELAY_BETWEEN_GAMES = 2000;   // wait two seconds

	private boolean running = false;
	private boolean stopAfterCurrentMatches = false;
	private final AbstractDBConnector<TermType, ReasonerStateInfoType> db;

	private Thread gameThread;
//	private List<Thread> matchThreads;

	private final GamePicker<TermType, ReasonerStateInfoType> gamePicker;
	private final PlayerStatusTracker<TermType, ReasonerStateInfoType> playerStatusTracker;
	private String lastPlayedGame = null;
	private int nbMatchesToPlayForGame;
	
	public AbstractRoundRobinScheduler(AbstractDBConnector<TermType, ReasonerStateInfoType> db) {
		this.db = db;
		this.gamePicker = new GamePicker<TermType, ReasonerStateInfoType>(db);
		this.playerStatusTracker = new PlayerStatusTracker<TermType, ReasonerStateInfoType>(db);
	}

	public synchronized void start() {
		if (!running) {
			setRunning(true);
			stopAfterCurrentMatches=false;
			
			gameThread=new Thread(){
				@Override
				public void run() {
					try {
						runMatches();
					} catch (SQLException e) {
						logger.severe("exception: " + e); //$NON-NLS-1$
					} catch (InterruptedException e) {
						logger.info("round robin scheduler interrupted");
					}
					setRunning(false);
				}
			};
			gameThread.start();
		}else if(stopAfterCurrentMatches){ // stop the stop
			stopAfterCurrentMatches = false;
		}
	}
	
	public synchronized void stop() {
		if (running) {
			stopAfterCurrentMatches = true;
			gameThread.interrupt();
			try {
				gameThread.join();
			} catch (InterruptedException e) {
			}
			setRunning(false);
		}
	}

	public void stopGracefully() {
		stopAfterCurrentMatches = true;
	}
	
	public synchronized Game<TermType, ReasonerStateInfoType> getNextPlayedGame() throws SQLException {
		return gamePicker.getNextPlayedGame();
	}

	public synchronized void setNextPlayedGame(String name) throws SQLException {
		gamePicker.setNextPlayedGame(name);
	}

	public boolean isRunning() {
		return running;
	}
	
	public boolean isBeingStopped() {
		return running && stopAfterCurrentMatches;
	}

	// this method has default visibility so it can be accessed from within the
	// anonymous inner Thread classes without overhead
	void setRunning(boolean running) {
		this.running = running;
	}

	// this method has default visibility so it can be accessed from within the
	// anonymous inner Thread classes without overhead
	void runMatches() throws SQLException, InterruptedException {
		while (!stopAfterCurrentMatches) {
			List<NewMatch<TermType, ReasonerStateInfoType>> currentMatches = createMatches();
			runMatches(currentMatches);
			Thread.sleep(DELAY_BETWEEN_GAMES);
		}
		setRunning(false);
	}

	protected abstract void runMatches(Collection<NewMatch<TermType, ReasonerStateInfoType>> currentMatches) throws SQLException;

	private List<NewMatch<TermType, ReasonerStateInfoType>> createMatches() throws SQLException, InterruptedException {
		int playclock;
		int startclock;
		
		if (logger.isLoggable(Level.CONFIG)) {
			// debug mode -- you can change this by editing the
			// logging.properties file (and starting the VM with special
			// arguments).
			// For now, this only really makes sense when executing
			// RoundRobinSchedulerTest. The reason to reduce start and play
			// clock is to speed up games.
			playclock = 5;
			startclock = 5;
		} else {
			int playclockMin = getPlayClockMin();
			int playclockMax = getPlayClockMax();
			int startclockMin = getStartClockMin();
			int startclockMax = getStartClockMax();
			
			// pick playclock as a multiple of 5 somewhere between playclockMin and playclockMax
			playclock =  playclockMin + 5 * random.nextInt( ( playclockMax - playclockMin) / 5 + 1 );
			// ((int) (random.nextDouble() * 12 + 1)) * 5;
			
			// the same for startclock
			startclock =  startclockMin + 5 * random.nextInt( ( startclockMax - startclockMin) / 5 + 1 );
			// startclock = 6 * playclock;
		}
		
		List<NewMatch<TermType, ReasonerStateInfoType>> result = new LinkedList<NewMatch<TermType,ReasonerStateInfoType>>();
		
		Game<TermType, ReasonerStateInfoType> nextGame = gamePicker.getNextPlayedGame();
		if( nextGame.getName().equals(lastPlayedGame) ) {
			nbMatchesToPlayForGame--;
		}else{
			nbMatchesToPlayForGame = nextGame.getNumberOfRoles() - 1;
		}
		
		if (nbMatchesToPlayForGame < 1) {
			// change next game to play
			gamePicker.pickNextGame();
		}
		Collection<? extends PlayerInfo> activePlayers = playerStatusTracker.waitForActivePlayers();
		// If there are no enabled games, gamePicker.pickNextGame() will return null.
		// In order to handle this case correctly, one would have to replace gamePicker.pickNextGame()
		// by some function waitForEnabledGames(), similar to playerStatusTracker.waitForActivePlayers().
		// ATM, we will just run into a NullPointerException somewhere down the line, so let's hope that
		// there is always at least one enabled game! :-)
		
		List<Map<RoleInterface<TermType>, PlayerInfo>> matchesToRolesToPlayerInfos = createPlayerInfos(nextGame, activePlayers);
		
		for (Map<RoleInterface<TermType>, PlayerInfo> rolesToPlayerInfos : matchesToRolesToPlayerInfos) {
			result.add(db.createMatch(nextGame, startclock, playclock, rolesToPlayerInfos, db.getTournament(ROUND_ROBIN_TOURNAMENT_ID)));
		}
		return result;
	}

	private int getIntOption(ConfigOption option) throws SQLException {
		String s = db.getConfigOption(option);
		try {
			return Integer.parseInt(s);
		} catch (NumberFormatException e) {
			logger.severe("NumberFormatException while parsing config option " + option + ":" + e.getMessage());
			return 42;
		}
	}
	
	private int getStartClockMax() throws SQLException {
		return getIntOption(ConfigOption.START_CLOCK_MAX);
	}

	private int getStartClockMin() throws SQLException {
		return getIntOption(ConfigOption.START_CLOCK_MIN);
	}

	private int getPlayClockMax() throws SQLException {
		return getIntOption(ConfigOption.PLAY_CLOCK_MAX);
	}

	private int getPlayClockMin() throws SQLException {
		return getIntOption(ConfigOption.PLAY_CLOCK_MIN);
	}

	private List<Map<RoleInterface<TermType>, PlayerInfo>> createPlayerInfos(Game<TermType, ReasonerStateInfoType> game, Collection<? extends PlayerInfo> activePlayers) {
		List<PlayerInfo> allPlayerInfos = new LinkedList<PlayerInfo>(activePlayers);
		int numberOfRoles = game.getNumberOfRoles();
		
		// add enough random players so that the players are divisible among the matches without remainder 
		int numberOfSurplusPlayers = allPlayerInfos.size() % numberOfRoles;		
		if (numberOfSurplusPlayers > 0) {
			int numberOfRandomPlayers = numberOfRoles - numberOfSurplusPlayers;
			for (int i = 0; i < numberOfRandomPlayers; i++) {
				allPlayerInfos.add(new RandomPlayerInfo(-1));
			}
		}
		
		Collections.shuffle(allPlayerInfos);

		int numberOfMatches = allPlayerInfos.size() / numberOfRoles;
		List<Map<RoleInterface<TermType>, PlayerInfo>> result = new ArrayList<Map<RoleInterface<TermType>, PlayerInfo>>(numberOfMatches);		
		for (int i = 0; i < numberOfMatches; i++) {
			List<PlayerInfo> playerInfos = allPlayerInfos.subList(i * numberOfRoles, (i + 1) * numberOfRoles);
			
			Map<RoleInterface<TermType>, PlayerInfo> roleMap = new HashMap<RoleInterface<TermType>, PlayerInfo>();
			
			for (int j = 0; j < numberOfRoles; j++) {
				playerInfos.get(j).setRoleindex(j);
				roleMap.put(game.getOrderedRoles().get(j), playerInfos.get(j));
			}
			result.add(roleMap);
		}
		
		return result;
	}
}
