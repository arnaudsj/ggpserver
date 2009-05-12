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
import java.util.List;
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
	
	private Map<String, Game<Term, GameState>> games;
	private Map<String, Match<Term, GameState>> matches;
	private Map<String, PlayerInfo> playerInfos;
	private Map<String, User> users;

	private DBConnector() {
		super();
		clearCache();   // to initialize the cache
	}

	public static DBConnector getInstance() {
		if (instance == null) {
			instance = new DBConnector();
		}
		return instance;
	}

	@Override
	@SuppressWarnings("unchecked")
	public  void clearCache() {
		games = new ReferenceMap(SOFT, SOFT, false);
		matches = new ReferenceMap(SOFT, SOFT, false);
		playerInfos = new ReferenceMap(SOFT, SOFT, false);
		users = new ReferenceMap(SOFT, SOFT, false);
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
	public void updateGameInfo(String gameName, String gameDescription,
			String stylesheet, boolean enabled) throws SQLException {
		super.updateGameInfo(gameName, gameDescription, stylesheet, enabled);
		clearCacheForGame(gameName);
	}

	private void clearCacheForGame(String gameName) throws SQLException {
		// delete cached result, so it will read again on next request
		games.remove(gameName);
		
		// also remove all cached matches of this game (to prevent the stale-stylesheet-bug).
		// This is horribly inefficient if only a small percentage of all matches for that game 
		// are in the cache, because getMatchesForGame(gameName) first creates all missing matches,
		// only to delete them afterwards. Perhaps it would be more efficient to just clear the
		// whole cache and be done with it.
		// However, since updating a game doesn't happen very often anyway, this whole issue
		// probably won't have much of a performance impact anyway.
		for (Match<Term, GameState> match : getMatchesForGame(gameName)) {
			matches.remove(match.getMatchID());
		}
	}

	private List<Match<Term, GameState>> getMatchesForGame(String gameName) throws SQLException {
		// The "0, Integer.MAX_VALUE" thing is a bit of a hack. 
		return getMatches(0, Integer.MAX_VALUE, null, gameName);
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
		super.updatePlayerInfo(playerName, host, port, user, status);
		clearCacheForPlayer(playerName);		
	}

	private void clearCacheForPlayer(String playerName) throws SQLException {
		// delete cached result, so it will read again on next request
		playerInfos.remove(playerName);
		
		// also remove all cached matches of this player. See comment for clearCacheForGame().
		for (Match<Term, GameState> match : getMatchesForPlayer(playerName)) {
			matches.remove(match.getMatchID());
		}
	}

	private List<Match<Term,GameState>> getMatchesForPlayer(String playerName) throws SQLException {
		// The "0, Integer.MAX_VALUE" thing is a bit of a hack. 
		return getMatches(0, Integer.MAX_VALUE, playerName, null);
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
