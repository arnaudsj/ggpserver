/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de>
                  2009 Stephan Schiffel <stephan.schiffel@gmx.de>

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
import tud.ggpserver.datamodel.matches.FinishedMatch;
import tud.ggpserver.datamodel.matches.NewMatch;

public abstract class AbstractRoundRobinScheduler<TermType extends TermInterface, ReasonerStateInfoType> {
	public static final String ROUND_ROBIN_TOURNAMENT_ID = "round_robin_tournament";
	private static final Logger logger = Logger.getLogger(AbstractRoundRobinScheduler.class.getName());
	private static final Random random = new Random();

	private boolean running = false;
	private boolean stopAfterCurrentMatches = false;
	private final AbstractDBConnector<TermType, ReasonerStateInfoType> db;

	private Thread gameThread;
	private final PlayerErrorTracker<TermType, ReasonerStateInfoType> playerErrorTracker;

	private final GamePicker<TermType, ReasonerStateInfoType> gamePicker;
	private String lastPlayedGame = null;
	private int nbMatchesToPlayForGame;
	
	public AbstractRoundRobinScheduler(AbstractDBConnector<TermType, ReasonerStateInfoType> db) {
		this.db = db;
		this.gamePicker = new GamePicker<TermType, ReasonerStateInfoType>(db);
		this.playerErrorTracker = new PlayerErrorTracker<TermType, ReasonerStateInfoType>(db);
	}

	public synchronized void start() {
		if (!running) {
			logger.info("starting RoundRobinScheduler");
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
			logger.info("RoundRobinScheduler is still running -> stop the stopping");
			stopAfterCurrentMatches = false;
		}
	}
	
	public synchronized void stop() {
		if (running) {
			logger.info("stopping RoundRobinScheduler");
			stopAfterCurrentMatches = true;
			gameThread.interrupt();
			try {
				gameThread.join();
			} catch (InterruptedException e) {
			}
			setRunning(false);
		}else{
			logger.info("RoundRobinScheduler is not running");
		}
	}

	public void stopGracefully() {
		logger.info("stopping RoundRobinScheduler after the currently running Matches");
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
	private void setRunning(boolean running) {
		this.running = running;
	}

	// this method has default visibility so it can be accessed from within the
	// anonymous inner Thread classes without overhead
	private void runMatches() throws SQLException, InterruptedException {
		while (!stopAfterCurrentMatches) {
			List<NewMatch<TermType, ReasonerStateInfoType>> currentMatches = createMatches();
			logger.info("running matches");
			getMatchRunner().runMatches(currentMatches);
			logger.info("matches of this round finished.");
			for(NewMatch<TermType, ReasonerStateInfoType> match:currentMatches) {
				try {
					FinishedMatch<TermType, ReasonerStateInfoType> finishedMatch = db.getFinishedMatch(match.getMatchID());
					playerErrorTracker.updateDeadPlayers(finishedMatch);
				} catch(IllegalArgumentException e) {
					// in case it is not a finished match (e.g., aborted)
				}
			}
		}
		setRunning(false);
	}

	protected abstract MatchRunner<TermType, ReasonerStateInfoType> getMatchRunner();

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
			int startclockMin = getStartClockMin();
			
			// pick playclock as a multiple of 5 somewhere between playclockMin and playclockMax distributed according to a logistic distribution
			playclock = 
						Math.min(
							playclockMin + 5 * (int)Math.round(
								logisticDistributionQuantile(random.nextDouble(), (getPlayClockMean() - playclockMin) / 5.0, getPlayClockStdDeviation() / 5.0)),
							getPlayClockMax()
							);
			// playclock =  playclockMin + 5 * random.nextInt( ( playclockMax - playclockMin) / 5 + 1 );
			
			// the same for startclock
			startclock = 
				Math.min(
					startclockMin + 5 * (int)Math.round(
						logisticDistributionQuantile(random.nextDouble(), (getStartClockMean() - startclockMin) / 5.0, getStartClockStdDeviation() / 5.0)),
					getStartClockMax()
					);
			// startclock =  startclockMin + 5 * random.nextInt( ( startclockMax - startclockMin) / 5 + 1 );
		}
		
		List<NewMatch<TermType, ReasonerStateInfoType>> result = new LinkedList<NewMatch<TermType,ReasonerStateInfoType>>();
		
		Game<TermType, ReasonerStateInfoType> nextGame = gamePicker.getNextPlayedGame();
		if( nextGame.getName().equals(lastPlayedGame) ) {
			nbMatchesToPlayForGame--;
		}else{
			nbMatchesToPlayForGame = nextGame.getNumberOfRoles() - 1;
			lastPlayedGame = nextGame.getName();
		}
		
		if (nbMatchesToPlayForGame < 1) {
			// change next game to play
			gamePicker.pickNextGame();
		}
		logger.info("waiting for some players to become available");
		Collection<? extends PlayerInfo> availablePlayers = MatchRunner.getInstance().waitForAvailablePlayers();
		logger.info("# available players: " + availablePlayers.size());
		// If there are no enabled games, gamePicker.pickNextGame() will return null.
		// In order to handle this case correctly, one would have to replace gamePicker.pickNextGame()
		// by some function waitForEnabledGames(), similar to playerStatusTracker.waitForActivePlayers().
		// ATM, we will just run into a NullPointerException somewhere down the line, so let's hope that
		// there is always at least one enabled game! :-)
		
		logger.info("associating players to roles");
		List<Map<RoleInterface<TermType>, PlayerInfo>> matchesToRolesToPlayerInfos = createPlayerInfos(nextGame, availablePlayers);
		
		logger.info("creating " + matchesToRolesToPlayerInfos.size() + " matches");
		for (Map<RoleInterface<TermType>, PlayerInfo> rolesToPlayerInfos : matchesToRolesToPlayerInfos) {
			result.add(db.createMatch(nextGame, startclock, playclock, rolesToPlayerInfos, ROUND_ROBIN_TOURNAMENT_ID));
		}
		logger.info("done creating matches");
		return result;
	}

	private double logisticDistributionQuantile(double probability, double mean, double stdDeviation) {
		return mean + Math.sqrt(3) * stdDeviation / Math.PI * Math.log(probability / (1 - probability));
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

	private int getStartClockMean() throws SQLException {
		return getIntOption(ConfigOption.START_CLOCK_MEAN);
	}

	private int getStartClockStdDeviation() throws SQLException {
		return getIntOption(ConfigOption.START_CLOCK_STD_DEVIATION);
	}

	private int getPlayClockMax() throws SQLException {
		return getIntOption(ConfigOption.PLAY_CLOCK_MAX);
	}

	private int getPlayClockMin() throws SQLException {
		return getIntOption(ConfigOption.PLAY_CLOCK_MIN);
	}

	private int getPlayClockMean() throws SQLException {
		return getIntOption(ConfigOption.PLAY_CLOCK_MEAN);
	}

	private int getPlayClockStdDeviation() throws SQLException {
		return getIntOption(ConfigOption.PLAY_CLOCK_STD_DEVIATION);
	}

	
	private List<Map<RoleInterface<TermType>, PlayerInfo>> createPlayerInfos(Game<TermType, ReasonerStateInfoType> game, Collection<? extends PlayerInfo> players) {
		List<PlayerInfo> allPlayerInfos = new LinkedList<PlayerInfo>(players);
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
