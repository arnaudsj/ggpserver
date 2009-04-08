package tud.ggpserver.scheduler;

import java.sql.SQLException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.naming.NamingException;

import tud.gamecontroller.GameController;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.AbstractReasonerFactory;
import tud.ggpserver.datamodel.DBConnector;
import tud.ggpserver.datamodel.Game;
import tud.ggpserver.datamodel.Match;
import tud.ggpserver.datamodel.RemotePlayerInfo;

public abstract class AbstractRoundRobinScheduler<TermType extends TermInterface, ReasonerStateInfoType> {
	private static final long DELAY_BETWEEN_GAMES = 2000;   // wait two seconds
	private Thread gameThread;
	private boolean running = false;
	private Game<TermType, ReasonerStateInfoType> currentGame;
	private Match<TermType, ReasonerStateInfoType> currentMatch;
	

	public AbstractRoundRobinScheduler() {
		Logger.getLogger("tud.gamecontroller").addHandler(new LoggingHandler(this));
	}

	protected abstract DBConnector<TermType, ReasonerStateInfoType> getDBConnector();

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
						e.printStackTrace();
					} catch (SQLException e) {
						e.printStackTrace();
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

	private Match<TermType, ReasonerStateInfoType> createMatch() throws NamingException, SQLException {
		List<Game<TermType, ReasonerStateInfoType>> allGames = getDBConnector().getAllGames(getReasonerFactory()); // TODO: this takes a looooong time!
		
		if (currentGame == null) {
			currentGame = allGames.get(0);
		} else {
			int nextGameIndex = (allGames.indexOf(currentGame) + 1) % allGames.size();
			currentGame = allGames.get(nextGameIndex);
		}
		
		Map<RoleInterface<TermType>, PlayerInfo> playerinfos = createPlayerInfos(currentGame);
		GameProperties props = GameProperties.getInstance(currentGame.getName());
		
		return getDBConnector().createMatch(currentGame, props.getStartClock(), props.getPlayClock(), playerinfos, new Date(), getReasonerFactory());
	}
	
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

	protected abstract AbstractReasonerFactory<TermType, ReasonerStateInfoType> getReasonerFactory();

	protected Match<TermType, ReasonerStateInfoType> getCurrentMatch() {
		return currentMatch;
	}
}
