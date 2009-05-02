package tud.ggpserver.scheduler;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import tud.gamecontroller.GameController;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.Game;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.Match;
import tud.ggpserver.datamodel.RemotePlayerInfo;

public abstract class AbstractRoundRobinScheduler<TermType extends TermInterface, ReasonerStateInfoType> {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(AbstractRoundRobinScheduler.class.getName());

	private static final long DELAY_BETWEEN_GAMES = 2000;   // wait two seconds
	private boolean running = false;
	private final AbstractDBConnector dbConnector;

	private Game<TermType, ReasonerStateInfoType> currentGame;
	private Thread gameThread;

	private List<Match<TermType, ReasonerStateInfoType>> currentMatches;
	private List<Thread> matchThreads;
		
	public AbstractRoundRobinScheduler(AbstractDBConnector dbConnector) {
		Logger.getLogger("tud.gamecontroller").addHandler(new LoggingHandler());
		this.dbConnector = dbConnector;
	}

	public void start() {
		if (!running) {
			setRunning(true);
			
			// TODO: clean out "new" and "running" matches
			
			
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
	
	private void runMatches() throws InterruptedException, SQLException {
		while (true) {
			currentMatches = createMatches();
			
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
	private List<Match<TermType, ReasonerStateInfoType>> createMatches() throws SQLException {
		pickNextGame();
		
		// pick playclock (5, 10, ..., 60 seconds)
//		int playclock = ((int) (new Random().nextDouble() * 12 + 1)) * 5;
//		int startclock = 6 * playclock;
		// TODO: only changed for debugging
		int playclock = 3;
		int startclock = 3;
		
		List<Match<TermType, ReasonerStateInfoType>> result = new LinkedList<Match<TermType,ReasonerStateInfoType>>();
		
		List<Map<RoleInterface<TermType>, PlayerInfo>> allPlayerInfos = createPlayerInfos(currentGame);
		for (Map<RoleInterface<TermType>, PlayerInfo> playerInfos : allPlayerInfos) {
			result.add(getDBConnector().createMatch(currentGame, startclock, playclock, playerInfos, new Date()));
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private void pickNextGame() throws SQLException {
		List<Game<TermType, ReasonerStateInfoType>> allGames = getDBConnector().getAllGames();
		
		if (currentGame == null) {
			// TODO: start first game with fewest matches (to evenly distribute matches among games)
			currentGame = allGames.get(0);
		} else {
			int nextGameIndex = (allGames.indexOf(currentGame) + 1) % allGames.size();
			currentGame = allGames.get(nextGameIndex);
		}
	}
	
	private List<Map<RoleInterface<TermType>, PlayerInfo>> createPlayerInfos(Game<TermType, ReasonerStateInfoType> game) throws SQLException {
		AbstractDBConnector<?, ?> db = getDBConnector();
		List<PlayerInfo> activePlayerInfos = db.getPlayerInfos(RemotePlayerInfo.STATUS_ACTIVE);		
		int numberOfRoles = game.getNumberOfRoles();
				
		// add enough random players so that the players are divisible among the matches without remainder 
		int numberOfSurplusPlayers = activePlayerInfos.size() % numberOfRoles;		
		if (numberOfSurplusPlayers > 0) {
			int numberOfRandomPlayers = numberOfRoles - numberOfSurplusPlayers;
			for (int i = 0; i < numberOfRandomPlayers; i++) {
				activePlayerInfos.add(new RandomPlayerInfo(-1));
			}
		}
		
		Collections.shuffle(activePlayerInfos);

		int numberOfMatches = activePlayerInfos.size() / numberOfRoles;
		List<Map<RoleInterface<TermType>, PlayerInfo>> result = new ArrayList<Map<RoleInterface<TermType>, PlayerInfo>>(numberOfMatches);		
		for (int i = 0; i < numberOfMatches; i++) {
			List<PlayerInfo> playerInfos = activePlayerInfos.subList(i * numberOfRoles, (i + 1) * numberOfRoles);
			
			Map<RoleInterface<TermType>, PlayerInfo> roleMap = new HashMap<RoleInterface<TermType>, PlayerInfo>();
			
			for (int j = 0; j < numberOfRoles; j++) {
				playerInfos.get(j).setRoleindex(j);
				roleMap.put(game.getOrderedRoles().get(j), playerInfos.get(j));
			}
			result.add(roleMap);
		}
		
		return result;
	}
	

	public void stop() {
		if (running) {
			gameThread.interrupt();
			try {
				gameThread.join();
			} catch (InterruptedException e) {
			}
		}
		currentMatches = null;
		setRunning(false);
	}

	public boolean isRunning() {
		return running;
	}

	private void setRunning(boolean running) {
		this.running = running;
	}

	private AbstractDBConnector getDBConnector() {
		return dbConnector;
	}
}
