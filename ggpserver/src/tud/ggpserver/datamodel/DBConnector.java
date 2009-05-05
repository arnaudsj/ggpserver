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

package tud.ggpserver.datamodel;

import static org.apache.commons.collections.map.AbstractReferenceMap.SOFT;

import java.sql.SQLException;
import java.util.Date;
import java.util.Map;

import org.apache.commons.collections.map.ReferenceMap;

import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.Move;
import tud.gamecontroller.game.impl.MoveFactory;
import tud.gamecontroller.game.javaprover.Reasoner;
import tud.gamecontroller.game.javaprover.Term;
import tud.gamecontroller.game.javaprover.TermFactory;
import tud.gamecontroller.players.PlayerInfo;
import cs227b.teamIago.util.GameState;

public class DBConnector extends AbstractDBConnector<Term, GameState> {
	private static DBConnector instance;
	
//	private static Map<String, Game<Term, GameState>> games = new HashMap<String, Game<Term, GameState>>();
//	private static Map<String, Match<Term, GameState>> matches = new HashMap<String, Match<Term,GameState>>();
//	private static Map<String, PlayerInfo> playerInfos = new HashMap<String, PlayerInfo>();
//	private static Map<String, User> users = new HashMap<String, User>();
	@SuppressWarnings("unchecked")
	private static Map<String, Game<Term, GameState>> games = new ReferenceMap(SOFT, SOFT, false);
	@SuppressWarnings("unchecked")
	private static Map<String, Match<Term, GameState>> matches = new ReferenceMap(SOFT, SOFT, false);
	@SuppressWarnings("unchecked")
	private static Map<String, PlayerInfo> playerInfos = new ReferenceMap(SOFT, SOFT, false);
	@SuppressWarnings("unchecked")
	private static Map<String, User> users = new ReferenceMap(SOFT, SOFT, false);

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
	@Override
	public Game<Term, GameState> createGame(String gameDescription, String name, String stylesheet, boolean enabled)
			throws DuplicateInstanceException, SQLException {
		Game<Term, GameState> result = super.createGame(gameDescription, name, stylesheet, enabled);
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

	@Override
	public void updateGameInfo(String name, String gameDescription, String stylesheet, boolean enabled) throws SQLException {
		super.updateGameInfo(name, gameDescription, stylesheet, enabled);
		// delete cached result, so it will read again on next request
		games.remove(name);
	}

	/////////////////// MATCH ///////////////////
	@Override
	public Match<Term, GameState> createMatch(
			String matchID,
			Game<Term, GameState> game,
			int startclock,
			int playclock,
			Map<? extends RoleInterface<Term>, ? extends PlayerInfo> rolesToPlayers,
			Date startTime) throws DuplicateInstanceException,
			SQLException {
		
		Match<Term, GameState> result = super.createMatch(matchID, game, startclock, playclock, rolesToPlayers, startTime);
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
	@Override
	public RemotePlayerInfo createPlayerInfo(String name, String host,
			int port, User owner, String status)
			throws DuplicateInstanceException, SQLException {
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
