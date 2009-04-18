package tud.ggpserver.datamodel;

import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;

import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.Game;
import tud.gamecontroller.game.impl.Move;
import tud.gamecontroller.game.impl.MoveFactory;
import tud.gamecontroller.game.javaprover.Reasoner;
import tud.gamecontroller.game.javaprover.Term;
import tud.gamecontroller.game.javaprover.TermFactory;
import tud.gamecontroller.players.PlayerInfo;
import cs227b.teamIago.util.GameState;

public class DBConnector extends AbstractDBConnector<Term, GameState> {
	private static DBConnector instance;

	private DBConnector() {
		super();
	}

	public static DBConnector getInstance() {
		if (instance == null) {
			instance = new DBConnector();
		}
		return instance;
	}

	@Override
	protected MoveFactoryInterface<Move<Term>> getMoveFactory() {
		return new MoveFactory<Term>(new TermFactory());
	}

	@Override
	protected ReasonerInterface<Term, GameState> getReasoner(
			String gameDescription, String name) {
		return new Reasoner(gameDescription);
	}

	/////////////////// GAME ///////////////////
	private static Map<String, Game<Term, GameState>> games = new HashMap<String, Game<Term, GameState>>();

	@Override
	public Game<Term, GameState> createGame(String gameDescription, String name)
			throws DuplicateInstanceException, SQLException {
		Game<Term, GameState> result = super.createGame(gameDescription, name);
		games.put(name, result);
		return result;
	}

	@Override
	public Game<Term, GameState> getGame(String name) throws SQLException {
		Game<Term, GameState> result = games.get(name);
		if (result == null) {
			result = super.getGame(name);

			if (result != null) {
				games.put(name, result);
			}
		}
		return result;
	}

	/////////////////// MATCH ///////////////////
	private static Map<String, Match<Term, GameState>> matches = new HashMap<String, Match<Term,GameState>>();
	
	@Override
	public Match<Term, GameState> createMatch(
			String matchID,
			Game<Term, GameState> game,
			int startclock,
			int playclock,
			Map<? extends RoleInterface<Term>, ? extends PlayerInfo> playerinfos,
			Date startTime) throws DuplicateInstanceException,
			SQLException {
		
		Match<Term, GameState> result = super.createMatch(matchID, game, startclock, playclock, playerinfos, startTime);
		matches.put(matchID, result);
		return result;
	}

	@Override
	public Match<Term, GameState> getMatch(String matchID)
			throws SQLException {
		Match<Term, GameState> result = matches.get(matchID);
		if (result == null) {
			result = super.getMatch(matchID);
			
			if (result != null) {
				matches.put(matchID, result);
			}
		}
		return result;
	}

	/////////////////// PLAYERINFO ///////////////////
	private static Map<String, PlayerInfo> playerInfos = new HashMap<String, PlayerInfo>();

	@Override
	public RemotePlayerInfo createPlayerInfo(String name, String host,
			int port, User owner, String status) throws NamingException,
			DuplicateInstanceException, SQLException {
		RemotePlayerInfo result = super.createPlayerInfo(name, host, port, owner, status);
		playerInfos.put(name, result);
		return result;
	}

	@Override
	public PlayerInfo getPlayerInfo(String name) throws SQLException {
		PlayerInfo result = playerInfos.get(name);
		if (result == null) {
			result = super.getPlayerInfo(name);

			if (result != null) {
				playerInfos.put(name, result);
			}
		}
		return result;
	}

	@Override
	public void updatePlayerInfo(String playerName, String host, int port,
			User user, String status) throws SQLException {
		// delete cached result, so it will read again on next request
		playerInfos.remove(playerName);
		
		super.updatePlayerInfo(playerName, host, port, user, status);
	}

	/////////////////// USER ///////////////////
	private static Map<String, User> users = new HashMap<String, User>();

	@Override
	public User createUser(String userName, String password)
			throws DuplicateInstanceException, SQLException {
		User result = super.createUser(userName, password);
		users.put(userName, result);
		return result;
	}

	@Override
	public User getUser(String userName) throws SQLException {
		User result = users.get(userName);
		if (result == null) {
			result = super.getUser(userName);
			
			if (result != null) {
				users.put(userName, result);
			}
		}
		return result;
	}
}
