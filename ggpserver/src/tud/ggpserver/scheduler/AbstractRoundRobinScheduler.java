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

	private final Map<PlayerInfo, Integer> numErrorMatches = Collections.synchronizedMap(new HashMap<PlayerInfo, Integer>());
	
	private static final int MAX_ERROR_MATCHES = 2;

	
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
							checkDeadPlayers(match);
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
	
	/**
	 * For each player, the number of matches where the player produced any
	 * error ON EACH STATE is recorded here. If there is at least one state in
	 * the game without an error, the match does not count as an "error match".
	 * Also, only matches in a row are counted, i.e. whenever the player plays a
	 * non-error match, this number is reset to "0". <br>
	 * 
	 * When a player has played MAX_ERROR_MATCHES in a row, its status is set  
	 * to "inactive".
	 */
	private void checkDeadPlayers(Match<TermType, ReasonerStateInfoType> match) {
		Collection<RemotePlayerInfo> remotePlayerInfos = new LinkedList<RemotePlayerInfo>();
		
		for (PlayerInfo info : match.getPlayerInfos()) {
			if (info instanceof RemotePlayerInfo) {
				remotePlayerInfos.add((RemotePlayerInfo) info);
			}
		}
		
		for (RemotePlayerInfo playerInfo : remotePlayerInfos) {
			if (isPlayerDead(match, playerInfo)) {
				Integer oldNumErrorMatches = numErrorMatches.get(playerInfo);
				if (oldNumErrorMatches == null) {
					oldNumErrorMatches = 0;
				}
				Integer newNumErrorMatches = oldNumErrorMatches + 1;
				numErrorMatches.put(playerInfo, newNumErrorMatches);
				
				if (newNumErrorMatches > MAX_ERROR_MATCHES) {
					// update player status --> inactive
					try {
						dbConnector.updatePlayerInfo(playerInfo.getName(),
								playerInfo.getHost(), playerInfo.getPort(),
								playerInfo.getOwner(),
								RemotePlayerInfo.STATUS_INACTIVE);
					} catch (SQLException e) {
						logger.severe("exception: " + e);
					}
					
					// add an informative error message to the match
					String message = "The status of player "
							+ playerInfo.getName()
							+ " was set to INACTIVE because it produced"
							+ " an error in each state of more than "
							+ MAX_ERROR_MATCHES + " matches in a row.";
					match.updateErrorMessage(new GameControllerErrorMessage(
							GameControllerErrorMessage.PLAYER_DISABLED,
							message, match, playerInfo.getName()));					
					logger.warning(message);
				}
			} else {
				numErrorMatches.put(playerInfo, 0);
			}
		}
	}

	private boolean isPlayerDead(Match<TermType, ReasonerStateInfoType> match, PlayerInfo playerInfo) {
		List<List<GameControllerErrorMessage>> errorMessages = match.getErrorMessagesForPlayer(playerInfo);
		
		assert (errorMessages.size() == match.getNumberOfStates());
		
		for (int i = 0; i < match.getNumberOfStates(); i++) {
			if (errorMessages.get(i).size() == 0) {
				// no error messages for this state
				return false;
			}
		}
		return true;
	}
}
