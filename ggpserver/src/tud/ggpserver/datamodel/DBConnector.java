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

package tud.ggpserver.datamodel;

import static org.apache.commons.collections.map.AbstractReferenceMap.SOFT;

import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import org.apache.commons.collections.map.ReferenceMap;

import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.ReasonerFactoryInterface;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.State;
import tud.gamecontroller.game.javaprover.ReasonerFactory;
import tud.gamecontroller.game.javaprover.Term;
import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.matches.NewMatch;
import tud.ggpserver.datamodel.matches.ServerMatch;
import tud.ggpserver.datamodel.statistics.GameStatistics;
import tud.ggpserver.datamodel.statistics.TournamentStatistics;
import cs227b.teamIago.util.GameState;

/**
 * To be threadsafe, all write access to the following things must be synchronized:
 *    playerInfos 
 *    matches
 *    games 
 *    users 
 *    instance
 * 
 * It is not enough to use synchronized collections because of scenarios like the one 
 * described in updatePlayerInfo(). 
 * 
 * Note: the necessity to synchronize everything means that ONLY A SINGLE THREAD per type
 * of information (e.g., matches) can access the database. For the more complex objects
 * like matches and games, this is paid off by the time (and memory!) it takes to create 
 * one of these. Users and players might be better off without caching, although these
 * don't account for the bulk of the database activity anyway. 
 * 
 * @author Martin Günther <mintar@gmx.de>
 *
 */
public class DBConnector extends AbstractDBConnector<Term, GameState> {
	private static DBConnector instance;
	
	@SuppressWarnings("unchecked")
	private Map<String, Game<Term, GameState>> games = new ReferenceMap(SOFT, SOFT, false);

	@SuppressWarnings("unchecked")
	private Map<String, GameStatistics<Term, GameState>> gameStatistics = new ReferenceMap(SOFT, SOFT, false);

	@SuppressWarnings("unchecked")
	private Map<String, ServerMatch<Term, GameState>> matches= new ReferenceMap(SOFT, SOFT, false);
	
	@SuppressWarnings("unchecked")
	private Map<String, PlayerInfo> playerInfos = new ReferenceMap(SOFT, SOFT, false);

	@SuppressWarnings("unchecked")
	private Map<String, User> users = new ReferenceMap(SOFT, SOFT, false);

	@SuppressWarnings("unchecked")
	private Map<String, Tournament<Term, GameState>> tournaments = new ReferenceMap(SOFT, SOFT, false);

	@SuppressWarnings("unchecked")
	private Map<String, TournamentStatistics<Term, GameState>> tournamentStatistics = new ReferenceMap(SOFT, SOFT, false);

	private DBConnector(ReasonerFactoryInterface<Term, GameState> reasonerFactory) {
		super(reasonerFactory);
	}


	/**
	 * Will be synchronized on DBConnector.class .
	 */
	public static synchronized DBConnector getInstance() {
		if (instance == null) {
			instance = new DBConnector(new ReasonerFactory());
		}
		return instance;
	}

	@Override
	public void clearCache() {
		synchronized (games) {
			games.clear();
		}
		synchronized (gameStatistics) {
			gameStatistics.clear();
		}
		synchronized (playerInfos) {
			playerInfos.clear();
		}
		synchronized (matches) {
			matches.clear();
		}
		synchronized (users) {
			users.clear();
		}
		synchronized (tournaments) {
			tournaments.clear();
		}
		synchronized (tournamentStatistics) {
			tournamentStatistics.clear();
		}
		
		// Run the garbage collection. The reason for doing this is that we 
		// can generate a heap dump afterwards that doesn't contain many stale
		// objects.
		System.gc();
	}
	
	/////////////////// GAME ///////////////////
	@Override
	public Game<Term, GameState> createGame(String gameDescription,
			String name, String stylesheet, String seesXMLRules,
			boolean enabled, User creator, GDLVersion gdlVersion)
			throws DuplicateInstanceException, SQLException {
		synchronized (games) {
			Game<Term, GameState> result = super.createGame(gameDescription, name, stylesheet, seesXMLRules, enabled, creator, gdlVersion);
			games.put(name, result);
			return result;
		}
	}

	@Override
	public Game<Term, GameState> getGame(String name) throws SQLException {
		Game<Term, GameState> result = games.get(name);
		if (result == null) {
			synchronized (games) {
				result = super.getGame(name);
	
				if (result != null) {
					games.put(name, result);
				}
			}
		}
		return result;
	}

	@Override
	public void updateGameInfo(String gameName, String gameDescription,
			String stylesheet, String seesXMLRules, boolean enabled, GDLVersion gdlVersion) throws SQLException {
		synchronized (games) {
			// this has to be synchronized to ensure that no stale result gets re-introduced into the 
			// cache after clearing it
			clearCacheForGame(gameName);
			super.updateGameInfo(gameName, gameDescription, stylesheet, seesXMLRules, enabled, gdlVersion);
		}
	}

	private void clearCacheForGame(String gameName) throws SQLException {
		// this has to be done first, or otherwise this call to getGame() will be cached
		Game<?, ?> game = getGame(gameName);
		
		synchronized (games) {
			// delete cached result, so it will be read again on next request
			games.remove(gameName);
		}

		clearCacheForGameStatistics(gameName);
		
		// also remove all cached matches of this game (to prevent the stale-stylesheet-bug).
		synchronized (matches) {
			for (ServerMatch<?, ?> match : new LinkedList<ServerMatch<?, ?>>(matches.values())) {
					// new LinkedList(...) necessary to avoid concurrent iteration and modification
				if (match.getGame().equals(game)) {
					matches.remove(match.getMatchID());
				}
			}
		}
	}

	@Override
	public GameStatistics<Term, GameState> getGameStatistics(String gameName) throws SQLException {
		GameStatistics<Term, GameState> result = gameStatistics.get(gameName);
		if (result == null) {
			synchronized (gameStatistics) {
				result = super.getGameStatistics(gameName);
	
				if (result != null) {
					gameStatistics.put(gameName, result);
				}
			}
		}
		return result;
	}

	private void clearCacheForGameStatistics(String gameName) {
		synchronized (gameStatistics) {
			gameStatistics.remove(gameName);
		}
	}

	/////////////////// MATCH ///////////////////
	@Override
	public NewMatch<Term, GameState> createMatch(
			String matchID,
			GameInterface<Term, State<Term, GameState>> game,
			int startclock,
			int playclock,
			Map<? extends RoleInterface<Term>, ? extends PlayerInfo> rolesToPlayerInfos,
			String tournamentID,
			Date startTime, boolean scrambled, double weight, User user) throws DuplicateInstanceException,
			SQLException {
		NewMatch<Term, GameState> result;
		synchronized (matches) {
			result = super.createMatch(matchID, game, 
					startclock, playclock, rolesToPlayerInfos, tournamentID, 
					startTime, scrambled, weight, user);
			matches.put(matchID, result); 
		}
		clearCacheForTournament(tournamentID);
		return result;
	}

	@Override
	public ServerMatch<Term, GameState> getMatch(String matchID)
			throws SQLException {
		ServerMatch<Term, GameState> result = matches.get(matchID);
		if (result == null) {
			synchronized (matches) {
				result = super.getMatch(matchID);
				
				if (result != null) {
					matches.put(matchID, result);
				}
			}
		}
		return result;
	}
	
	@Override
	public void deleteMatch(String matchID) throws SQLException {
		ServerMatch<Term, GameState> match = getMatch(matchID);
		String tournamentID = match.getTournamentID();
		String gameName = match.getGame().getName();
		synchronized (matches) {
			clearCacheForMatch(matchID);
			super.deleteMatch(matchID);
		}
		clearCacheForTournament(tournamentID);
		clearCacheForTournamentStatistics(tournamentID);
		clearCacheForGameStatistics(gameName);
	}
	
	@Override
	public void setMatchStatus(String matchID, String status) throws SQLException {
		ServerMatch<Term, GameState> match = getMatch(matchID);
		String tournamentID = match.getTournamentID();
		String gameName = match.getGame().getName();
		synchronized (matches) {
			clearCacheForMatch(matchID);
			super.setMatchStatus(matchID, status);
		}
		if(status.equals(ServerMatch.STATUS_FINISHED)) {
			clearCacheForTournamentStatistics(tournamentID);
			clearCacheForGameStatistics(gameName);
		}
	}

	
	@Override
	public void setMatchWeight(String matchID, double weight) throws SQLException {
		ServerMatch<Term, GameState> match = getMatch(matchID);
		String tournamentID = match.getTournamentID();
		synchronized (matches) {
			clearCacheForMatch(matchID);
			super.setMatchWeight(matchID, weight);
		}
		if(match.getStatus().equals(ServerMatch.STATUS_FINISHED)){
			clearCacheForTournamentStatistics(tournamentID);
		}
	}


	@Override
	public void setMatchPlayclock(String matchID, int playclock) throws SQLException {
		synchronized (matches) {
			clearCacheForMatch(matchID);
			super.setMatchPlayclock(matchID, playclock);
		}
	}


	@Override
	public void setMatchPlayerInfo(String matchID, int roleNumber, PlayerInfo playerInfo) throws SQLException {
		synchronized (matches) {
			clearCacheForMatch(matchID);
			super.setMatchPlayerInfo(matchID, roleNumber, playerInfo);
		}
	}


	@Override
	public void setMatchStartclock(String matchID, int startclock) throws SQLException {
		synchronized (matches) {
			clearCacheForMatch(matchID);
			super.setMatchStartclock(matchID, startclock);
		}
	}

	
	@Override
	public void setMatchGame(NewMatch<Term, GameState> match, GameInterface<Term, State<Term, GameState>> newGame) throws SQLException {
		synchronized (matches) {
			clearCacheForMatch(match.getMatchID());
			super.setMatchGame(match, newGame);
		}
	}

	@Override
	public void setMatchScrambled(String matchID, boolean scrambled) throws SQLException {
		synchronized (matches) {
			clearCacheForMatch(matchID);
			super.setMatchScrambled(matchID, scrambled);
		}
	}


	@Override
	public void setMatchGoalValues(ServerMatch<Term, GameState> match, Map<? extends RoleInterface<?>, Integer> goalValues) throws SQLException {
		String tournamentID = match.getTournamentID();
		String gameName = match.getGame().getName();
		synchronized (matches) {
			clearCacheForMatch(match.getMatchID());
			super.setMatchGoalValues(match, goalValues);
		}
		clearCacheForTournamentStatistics(tournamentID);
		clearCacheForGameStatistics(gameName);
	}


	private void clearCacheForMatch(String matchID) {
		synchronized (matches) {
			matches.remove(matchID);
		}
	}


	/////////////////// PLAYERINFO ///////////////////
	@Override
	public RemotePlayerInfo createPlayerInfo(String name, String host,
			int port, User owner, String status, GDLVersion gdlVersion)
			throws DuplicateInstanceException, SQLException {
		synchronized (playerInfos) {
			RemotePlayerInfo result = super.createPlayerInfo(name, host, port, owner, status, gdlVersion);
			playerInfos.put(name, result);
			return result;
		}
	}

	@Override
	public PlayerInfo getPlayerInfo(String name) throws SQLException {
		PlayerInfo result = playerInfos.get(name);
		if (result == null) {
			synchronized (playerInfos) {
				result = super.getPlayerInfo(name);
	
				if (result != null) {
					playerInfos.put(name, result);
				}
			}
		}
		return result;
	}

	/**
	 * This method must be synchronized with getPlayerInfo(). Otherwise, the following could happen:
	 * - Thread 1: updatePlayerInfo()  [changes player status to STATUS_ACTIVE]
	 * - Thread 1: clears player from cache
	 * - Thread 2: getPlayerInfo(), re-adds player to cache
	 * - Thread 1: super.updatePlayerInfo() calls getPlayerInfo(), passes stale result 
	 *   (status == STATUS_INACTIVE) to notifyPlayerStatusChange(), which will think that 
	 *   the state was changed to STATUS_INACTIVE instead of STATUS_ACTIVE.   
	 */
	@Override
	public void updatePlayerInfo(String playerName, String host, int port,
			User user, String status, boolean availableForRoundRobinMatches, boolean availableForManualMatches, int gdlVersion) throws SQLException {
		synchronized (playerInfos) {
			clearCacheForPlayer(playerName);		
			super.updatePlayerInfo(playerName, host, port, user, status, availableForRoundRobinMatches, availableForManualMatches, gdlVersion);
		}
	}

	private void clearCacheForPlayer(String playerName) throws SQLException {
		// this has to be done first, or otherwise this call to getPlayerInfo() will be cached
		PlayerInfo player = getPlayerInfo(playerName);
		
		// delete cached result, so it will be read again on next request
		synchronized (playerInfos) {
			playerInfos.remove(playerName);
		}
		
		// remove all cached matches of this player. 
		synchronized (matches) {
			for (ServerMatch<?, ?> match : new LinkedList<ServerMatch<Term, GameState>>(matches.values())) {
					// new LinkedList(...) necessary to avoid concurrent iteration and modification
				if (match.getPlayerInfos().contains(player)) {
					clearCacheForMatch(match.getMatchID());
				}
			}
		}
	}

	/////////////////// USER ///////////////////
	@Override
	public User createUser(String userName, String password, String emailAddress)
			throws DuplicateInstanceException, SQLException {
		synchronized (users) {
			User result = super.createUser(userName, password, emailAddress);
			users.put(userName, result);
			return result;
		}
	}

	@Override
	public User getUser(String userName) throws SQLException {
		User result = users.get(userName);
		if (result == null) {
			synchronized (users) {
				result = super.getUser(userName);
				
				if (result != null) {
					users.put(userName, result);
				}
			}
		}
		return result;
	}
	
	/////////////////// TOURNAMENT ///////////////////
	@Override
	public Tournament<Term, GameState> getTournament(String tournamentID) throws SQLException {
		Tournament<Term, GameState> result = tournaments.get(tournamentID);
		if (result == null) {
			synchronized (tournaments) {
				result = super.getTournament(tournamentID);
				
				if (result != null) {
					tournaments.put(tournamentID, result);
				}
			}
		}
		return result;
	}

	private void clearCacheForTournament(String tournamentID) {
		synchronized (tournaments) {
			tournaments.remove(tournamentID);
		}
	}

	@Override
	public TournamentStatistics<Term, GameState> getTournamentStatistics(String tournamentID) throws SQLException {
		TournamentStatistics<Term, GameState> result = tournamentStatistics.get(tournamentID);
		if (result == null) {
			synchronized (tournamentStatistics) {
				result = super.getTournamentStatistics(tournamentID);
				
				if (result != null) {
					tournamentStatistics.put(tournamentID, result);
				}
			}
		}
		return result;
	}

	private void clearCacheForTournamentStatistics(String tournamentID) {
		synchronized (tournamentStatistics) {
			tournamentStatistics.remove(tournamentID);
		}
	}

}
