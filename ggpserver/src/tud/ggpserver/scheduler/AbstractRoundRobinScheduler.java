package tud.ggpserver.scheduler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import tud.gamecontroller.GameController;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.Game;
import tud.ggpserver.datamodel.Match;
import tud.ggpserver.datamodel.PlayerStatusTracker;

public abstract class AbstractRoundRobinScheduler<TermType extends TermInterface, ReasonerStateInfoType> {
	private static final Logger logger = Logger.getLogger(AbstractRoundRobinScheduler.class.getName());
	private static final Random random = new Random();
	private static final long DELAY_BETWEEN_GAMES = 2000;   // wait two seconds

	private boolean running = false;
	private final AbstractDBConnector dbConnector;

	private Thread gameThread;
	private List<Thread> matchThreads;

	private final GamePicker<TermType, ReasonerStateInfoType> gamePicker;
	private final PlayerStatusTracker<TermType, ReasonerStateInfoType> playerStatusTracker;

	
	public AbstractRoundRobinScheduler(AbstractDBConnector dbConnector) {
		Logger.getLogger("tud.gamecontroller").addHandler(new LoggingHandler());
		this.dbConnector = dbConnector;
		this.gamePicker = new GamePicker<TermType, ReasonerStateInfoType>(dbConnector);
		this.playerStatusTracker = new PlayerStatusTracker<TermType, ReasonerStateInfoType>(dbConnector);
	}

	public void start() {
		if (!running) {
			setRunning(true);
			
			gameThread=new Thread(){
				public void run() {
					try {
						runMatches();
					} catch (InterruptedException e) {
						// abort all current matches
						if (matchThreads != null) {
							try {
								for (Thread matchThread : matchThreads) {
									matchThread.interrupt();
									matchThread.join();
								}
							} catch (InterruptedException e1) {
								logger.severe("exception: " + e1); //$NON-NLS-1$
							}
						}
					} catch (SQLException e) {
						logger.severe("exception: " + e); //$NON-NLS-1$
					}
					setRunning(false);
				}
			};
			gameThread.start();
		}
	}
	
	public void stop() {
		if (running) {
			gameThread.interrupt();
			try {
				gameThread.join();
			} catch (InterruptedException e) {
			}
			setRunning(false);
		}
	}

	public boolean isRunning() {
		return running;
	}

	private void setRunning(boolean running) {
		this.running = running;
	}

	private void runMatches() throws InterruptedException, SQLException {
		while (true) {
			List<Match<TermType, ReasonerStateInfoType>> currentMatches = createMatches();
			
			matchThreads = new LinkedList<Thread>();
			
			for (final Match<TermType, ReasonerStateInfoType> currentMatch : currentMatches) {
				Thread thread = new Thread(){
					public void run() {
						Match<TermType, ReasonerStateInfoType> match = currentMatch;
						
						logger.info("Thread for match " + match.getMatchID() + " - START");						
						try {
							GameController<TermType, ReasonerStateInfoType> gameController = new GameController<TermType, ReasonerStateInfoType>(match);
							gameController.addListener(match);
							gameController.runGame();
							playerStatusTracker.updateDeadPlayers(match);
						} catch (InterruptedException e) {
							logger.info("Thread for match " + match.getMatchID() + " - INTERRUPT");
							match.updateStatus(Match.STATUS_ABORTED);
							match.updateErrorMessage(new GameControllerErrorMessage(GameControllerErrorMessage.ABORTED, "The match was aborted."));
						}
						logger.info("Thread for match " + match.getMatchID() + " - END");
					}
				};
				thread.start();
				matchThreads.add(thread);
			}
			
			// wait for all matches to complete
			for (Thread thread : matchThreads) {
				thread.join();
			}
			
			matchThreads = null;
			
			Thread.sleep(DELAY_BETWEEN_GAMES);
		}
	}

	@SuppressWarnings("unchecked")
	private List<Match<TermType, ReasonerStateInfoType>> createMatches() throws SQLException, InterruptedException {
		int playclock;
		int startclock;
		
//		if (logger.isLoggable(Level.CONFIG)) {
			// debug mode -- you can change this by editing the
			// logging.properties file (and starting the VM with special
			// arguments).
			// For now, this only really makes sense when executing
			// RoundRobinSchedulerTest. The reason to reduce start and play
			// clock is to speed up games.
			playclock = 5;
			startclock = 5;
//		} else {
//			// pick playclock (5, 10, ..., 60 seconds)
//			playclock = ((int) (random.nextDouble() * 12 + 1)) * 5;
//			startclock = 6 * playclock;
//		}
		
		List<Match<TermType, ReasonerStateInfoType>> result = new LinkedList<Match<TermType,ReasonerStateInfoType>>();
		
		Game<TermType, ReasonerStateInfoType> nextGame = gamePicker.pickNextGame();
		Collection<? extends PlayerInfo> activePlayers = playerStatusTracker.waitForActivePlayers();
		
		List<Map<RoleInterface<TermType>, PlayerInfo>> matchesToRolesToPlayers = createPlayerInfos(nextGame, activePlayers);
		
		for (Map<RoleInterface<TermType>, PlayerInfo> rolesToPlayers : matchesToRolesToPlayers) {
			result.add(getDBConnector().createMatch(nextGame, startclock, playclock, rolesToPlayers, new Date()));
		}
		return result;
	}

	private List<Map<RoleInterface<TermType>, PlayerInfo>> createPlayerInfos(Game<TermType, ReasonerStateInfoType> game, Collection<? extends PlayerInfo> activePlayers) throws SQLException {
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

	private AbstractDBConnector getDBConnector() {
		return dbConnector;
	}
}
