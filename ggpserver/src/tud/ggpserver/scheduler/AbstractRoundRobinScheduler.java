package tud.ggpserver.scheduler;

import static tud.ggpserver.datamodel.DBConnectorFactory.getDBConnector;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Logger;

import javax.naming.NamingException;

import tud.gamecontroller.GameController;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.Game;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.Match;
import tud.ggpserver.datamodel.RemotePlayerInfo;

public abstract class AbstractRoundRobinScheduler<TermType extends TermInterface, ReasonerStateInfoType> {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(AbstractRoundRobinScheduler.class.getName());

	private static final long DELAY_BETWEEN_GAMES = 2000;   // wait two seconds
	private Thread gameThread;
	private boolean running = false;
	private Game<TermType, ReasonerStateInfoType> currentGame;
	private Match<TermType, ReasonerStateInfoType> currentMatch;
	
	

	public AbstractRoundRobinScheduler() {
		Logger.getLogger("tud.gamecontroller").addHandler(new LoggingHandler(this));
	}

	public void start() {
		if (!running) {
			setRunning(true);
			gameThread=new Thread(){
				public void run() {
					try {
						runMatches();
					} catch (InterruptedException e) {
						if (currentMatch != null) {
							currentMatch.updateStatus(Match.STATUS_ABORTED);
							currentMatch.updateErrorMessage(new GameControllerErrorMessage(GameControllerErrorMessage.ABORTED, "The match was aborted."));
						}
					} catch (NamingException e) {
						logger.severe("exception: " + e); //$NON-NLS-1$
					} catch (SQLException e) {
						logger.severe("exception: " + e); //$NON-NLS-1$
					}
					setRunning(false);
				}
			};
			gameThread.start();
		}
	}
	
	private void runMatches() throws InterruptedException, NamingException, SQLException {
		while (true) {
			currentMatch = createMatch();
			
			GameController<TermType, ReasonerStateInfoType> gameController = new GameController<TermType, ReasonerStateInfoType>(currentMatch);
			gameController.addListener(currentMatch);

			gameController.runGame();
			Thread.sleep(DELAY_BETWEEN_GAMES);
		}
	}

	@SuppressWarnings("unchecked")
	private Match<TermType, ReasonerStateInfoType> createMatch() throws NamingException, SQLException {
		List<Game<TermType, ReasonerStateInfoType>> allGames = getDBConnector().getAllGames();
		
		if (currentGame == null) {
			currentGame = allGames.get(0);
		} else {
			int nextGameIndex = (allGames.indexOf(currentGame) + 1) % allGames.size();
			currentGame = allGames.get(nextGameIndex);
		}
		
		Map<RoleInterface<TermType>, PlayerInfo> playerinfos = createPlayerInfos(currentGame);
		
		
		// pick playclock (5, 10, ..., 60 seconds)
		int playclock = ((int) (new Random().nextDouble() * 12 + 1)) * 5;
		int startclock = 6 * playclock;
		
		return getDBConnector().createMatch(currentGame, startclock, playclock, playerinfos, new Date());
	}
	
	@SuppressWarnings("unchecked")
	private Map<RoleInterface<TermType>, PlayerInfo> createPlayerInfos(Game<TermType, ReasonerStateInfoType> game) throws NamingException, SQLException {
		// copy players and shuffle
		List<PlayerInfo> playerInfos = new LinkedList<PlayerInfo>(getDBConnector().getPlayerInfos(RemotePlayerInfo.STATUS_ACTIVE));
		Collections.shuffle(playerInfos);
		
		int numberOfPlayers = playerInfos.size();
		int numberOfRoles = game.getNumberOfRoles();

		if (numberOfPlayers > numberOfRoles) {
			// truncate player list to number of roles
			playerInfos.subList(numberOfRoles, numberOfPlayers).clear();
		} else {
			// fill up with random players and shuffle again
			for (int i = numberOfPlayers; i < numberOfRoles; i++) {
				playerInfos.add(new RandomPlayerInfo(-1));
			}
			Collections.shuffle(playerInfos);
		}
		
		// make result  
		Map<RoleInterface<TermType>, PlayerInfo> result = new HashMap<RoleInterface<TermType>, PlayerInfo>();
		
		for (int i = 0; i < numberOfRoles; i++) {
			playerInfos.get(i).setRoleindex(i);
			result.put(game.getOrderedRoles().get(i), playerInfos.get(i));
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
		currentMatch = null;
		setRunning(false);
	}

	public boolean isRunning() {
		return running;
	}

	private void setRunning(boolean running) {
		this.running = running;
	}

	protected Match<TermType, ReasonerStateInfoType> getCurrentMatch() {
		return currentMatch;
	}
}
