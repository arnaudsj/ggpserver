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

import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import tud.gamecontroller.ReasonerFactory;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.State;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.LegalPlayerInfo;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.gamecontroller.scrambling.GameScrambler;
import tud.gamecontroller.scrambling.GameScramblerInterface;
import tud.gamecontroller.scrambling.IdentityGameScrambler;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.matches.AbortedMatch;
import tud.ggpserver.datamodel.matches.FinishedMatch;
import tud.ggpserver.datamodel.matches.NewMatch;
import tud.ggpserver.datamodel.matches.RunningMatch;
import tud.ggpserver.datamodel.matches.ScheduledMatch;
import tud.ggpserver.datamodel.matches.ServerMatch;
import tud.ggpserver.datamodel.statistics.GamePlayerStatistics;
import tud.ggpserver.datamodel.statistics.GameRoleStatistics;
import tud.ggpserver.datamodel.statistics.GameStatistics;
import tud.ggpserver.datamodel.statistics.PerformanceInformation;
import tud.ggpserver.datamodel.statistics.TournamentStatistics;
import tud.ggpserver.scheduler.PlayerStatusListener;
import tud.ggpserver.util.Digester;

import com.mysql.jdbc.exceptions.MySQLIntegrityConstraintViolationException;


/**
 * The get###() methods return a new instance of ###, if it is
 * already in the database, else null. <br />
 * 
 * The create###() methods create a new instance of ###, add it to the
 * database and return it. If an object with the same key already exists in the database, a
 * DuplicateInstanceException will be thrown. <br />
 * 
 * @author martin
 * 
 */
public abstract class AbstractDBConnector<TermType extends TermInterface, ReasonerStateInfoType> {
	public static final String PLAYER_LEGAL = "Legal";

	public static final String PLAYER_RANDOM = "Random";

	public static final int MATCHID_MAXLENGTH = 40;

	private static final Logger logger = Logger.getLogger(AbstractDBConnector.class.getName());
	private static final GameScramblerInterface identityGameScrambler = new IdentityGameScrambler();

	private static final Collection<String> defaultRoleNames = Collections.<String>singleton("member");
	
	private static DataSource datasource;
	
	private Collection<PlayerStatusListener> playerStatusListeners = new ArrayList<PlayerStatusListener>();

	private final ReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory;

	
	public AbstractDBConnector(final ReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory) {
		this.reasonerFactory = reasonerFactory;
	}

	protected abstract MoveFactoryInterface<? extends MoveInterface<TermType>> getMoveFactory();


	public void clearCache() {
		// this class doesn't cache anything, so there is nothing to do here.
	}
	
	public User createUser(String userName, String password) throws DuplicateInstanceException, SQLException {
		Connection con = getConnection(); 
		PreparedStatement ps = null;
		
		try { 
//			String hashedPass = RealmBase.Digest(password, "SHA-1", null);
			String hashedPass = Digester.digest(password, "SHA-1");
			
			ps = con.prepareStatement("INSERT INTO `users` (`user_name`, `user_pass`) VALUES (?, ?);");
			ps.setString(1, userName);
			ps.setString(2, hashedPass);
			ps.executeUpdate();
			try {ps.close();} catch (SQLException e) {}

			ps = con.prepareStatement("INSERT INTO `user_roles` (`user_name`, `role_name`) VALUES (?, 'member');");
			ps.setString(1, userName);
			ps.executeUpdate();
			
		} catch (NoSuchAlgorithmException e) {
			throw new InternalError("java.security.MessageDigest supports SHA-1, so we shouldn't ever get this error!?");
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 

		logger.info("Creating new user: " + userName); //$NON-NLS-1$
		return new User(userName, defaultRoleNames);
	}

	public User getUser(String userName) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;
		User result = null;

		try { 
//			ps = con.prepareStatement("SELECT `user_name` FROM `users` WHERE `user_name` = ? ;");
//			ps = con.prepareStatement("SELECT `role_name` FROM `user_roles` WHERE `user_name` = ? ;");   // (*)
			ps = con.prepareStatement("SELECT `r`.`user_name`, `r`.`role_name` " + 
					"FROM `users` AS `u`, `user_roles` as `r` " + 
					"WHERE `u`.`user_name` = `r`.`user_name` AND `u`.`user_name` = ? ;");  // (**)
			// strictly speaking, query (**) could by replaced by (*), but (**) also checks if the user exists in table "users". 
			
			ps.setString(1, userName);
			rs = ps.executeQuery();
			
			Collection<String> roleNames = new ArrayList<String>(2);
			while (rs.next()) {
				roleNames.add(rs.getString("role_name"));
			}
			if (roleNames.isEmpty()) {
				// either the user doesn't exist in the "users" table, or he/she
				// has no roles assigned in the "user_roles" table.
				return null;
			}
			
			// logger.info("Returning new User: " + userName); //$NON-NLS-1$
			result = new User(userName, roleNames);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 
		return result;
	}
	
	public RemotePlayerInfo createPlayerInfo(String name, String host,
			int port, User owner, String status)
			throws DuplicateInstanceException, SQLException {
		
		assert(!name.equals(PLAYER_LEGAL));
		assert(!name.equals(PLAYER_RANDOM));

		Connection con = getConnection();
		PreparedStatement ps = null;
		try { 

			ps = con.prepareStatement("INSERT INTO `players` (`name` , `host` , `port` , `owner`, `status`, `plays_round_robin`, `plays_manual`) VALUES (?, ?, ?, ?, ?, ?, ?);");
			ps.setString(1, name);
			ps.setString(2, host);
			ps.setInt(3, port);
			ps.setString(4, owner.getUserName());
			ps.setString(5, status);
			ps.setBoolean(6, true);
			ps.setBoolean(7, true);
			
			ps.executeUpdate();
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 

		logger.info("Creating new RemotePlayerInfo: " + name); //$NON-NLS-1$
		RemotePlayerInfo result = new RemotePlayerInfo(name, host, port, owner, status, true, true);
		
		notifyPlayerStatusListeners(result);
		return result;
	}

	private void notifyPlayerStatusListeners(RemotePlayerInfo result) {
		for (PlayerStatusListener listener : playerStatusListeners) {
			listener.notifyStatusChange(result);
		}
	}
	
	public PlayerInfo getPlayerInfo(String name) throws SQLException {
		if (name.equals(PLAYER_LEGAL)) {
			// logger.info("Returning new LegalPlayerInfo"); //$NON-NLS-1$
			return new LegalPlayerInfo(-1);
		} else if (name.equals(PLAYER_RANDOM)) {
			// logger.info("Returning new RandomPlayerInfo"); //$NON-NLS-1$
			return new RandomPlayerInfo(-1);
		}
		
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		RemotePlayerInfo result = null;
		
		try { 
			
			ps = con.prepareStatement("SELECT `host` , `port` , `owner` , `status`, `plays_round_robin`, `plays_manual` FROM `players` WHERE `name` = ? ;");
			ps.setString(1, name);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				// logger.info("Returning new RemotePlayerInfo: " + name); //$NON-NLS-1$
				result = new RemotePlayerInfo(name, rs.getString("host"), rs.getInt("port"), getUser(rs.getString("owner")), rs.getString("status"), rs.getBoolean("plays_round_robin"), rs.getBoolean("plays_manual"));
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}

	public Game<TermType, ReasonerStateInfoType> createGame(String gameDescription,
			String name, String stylesheet, boolean enabled) throws DuplicateInstanceException,
			SQLException {
		
		Connection con = getConnection();
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement("INSERT INTO `games` (`name` , `gamedescription` , `stylesheet`, `enabled`) VALUES (?, ?, ?, ?);");
			ps.setString(1, name);
			ps.setString(2, gameDescription);
			ps.setString(3, stylesheet);
			ps.setBoolean(4, enabled);
			
			ps.executeUpdate();
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 

		logger.info("Creating new game: " + name); //$NON-NLS-1$
		return new Game<TermType, ReasonerStateInfoType>(gameDescription, name, reasonerFactory, stylesheet, enabled);
	}

	public Game<TermType, ReasonerStateInfoType> getGame(String name) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		Game<TermType,ReasonerStateInfoType> result = null;
		
		try { 
			ps = con.prepareStatement("SELECT `gamedescription` , `stylesheet`, `enabled` FROM `games` WHERE `name` = ? ;");
			ps.setString(1, name);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				String gameDescription = rs.getString("gamedescription");
				String stylesheet = rs.getString("stylesheet");
				boolean enabled = rs.getBoolean("enabled");
				
				// logger.info("Returning new game: " + name); //$NON-NLS-1$
				result = new Game<TermType, ReasonerStateInfoType>(gameDescription, name, reasonerFactory, stylesheet, enabled);
				
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}
	
	public NewMatch<TermType, ReasonerStateInfoType> createMatch(
			String matchID,
			GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game,
			int startclock,
			int playclock,
			Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> rolesToPlayerInfos,
			String tournamentID,
			Date startTime, 
			boolean scrambled, double weight, User user) throws DuplicateInstanceException, SQLException {

		if (matchID == null) {
			throw new IllegalArgumentException("matchID == null");
		}
		if (game == null) {
			throw new IllegalArgumentException("game == null");
		}
		if (startclock <= 0) {
			throw new IllegalArgumentException("startclock <= 0: " + startclock);
		}
		if (playclock <= 0) {
			throw new IllegalArgumentException("playclock <= 0: " + playclock);
		}
		if (rolesToPlayerInfos == null) {
			throw new IllegalArgumentException("rolesToPlayerInfos == null");
		}
		if (tournamentID == null) {
			throw new IllegalArgumentException("tournamentID == null");
		}
		if (startTime == null) {
			throw new IllegalArgumentException("startTime == null");
		}
		if (game.getNumberOfRoles() != rolesToPlayerInfos.size()) {
			throw new IllegalArgumentException("rolesToPlayerInfos.size() == " + rolesToPlayerInfos.size() + " but game has " + game.getNumberOfRoles() + " roles");
		}
		if (user == null) {
			throw new IllegalArgumentException("user == null");
		}
		
		Connection con = getConnection();
		PreparedStatement ps = null;
		try {

			ps = con.prepareStatement("INSERT INTO `matches` (`match_id` , `game` , `start_clock` , `play_clock` , `start_time`, `tournament_id`, `scrambled`, `weight`, `owner`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?);");
			ps.setString(1, matchID);
			ps.setString(2, game.getName());
			ps.setInt(3, startclock);
			ps.setInt(4, playclock);
			ps.setTimestamp(5, new Timestamp(startTime.getTime()));
			ps.setString(6, tournamentID);
			ps.setBoolean(7, scrambled);
			ps.setDouble(8, weight);
			ps.setString(9, user.getUserName());
			
			ps.executeUpdate();
			ps.close();
			
			// players
			ps = con.prepareStatement("INSERT INTO `match_players` (`match_id` , `player` , `roleindex`) VALUES (?, ?, ?);");
			
			ps.setString(1, matchID);
			
			Integer roleindex = 0;
			for (RoleInterface<TermType> role : game.getOrderedRoles()) {
				ps.setString(2, rolesToPlayerInfos.get(role).getName());
				ps.setInt(3, roleindex);
		
				ps.executeUpdate();
				roleindex++;
			}
			
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		}

		logger.info("Creating new match: " + matchID); //$NON-NLS-1$
		return new NewMatch<TermType, ReasonerStateInfoType>(matchID, game, startclock, playclock,
				rolesToPlayerInfos, startTime, scrambled, tournamentID, weight, user, this);
	}
	
	/**
	 * Creates a match with a generated match ID.
	 */
	public NewMatch<TermType, ReasonerStateInfoType> createMatch(GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game,
			int startclock, int playclock, Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> rolesToPlayerInfos,
			String tournamentID, Date startTime, boolean scrambled, double weight, User user) throws SQLException {
		
		long number = System.currentTimeMillis();
		NewMatch<TermType, ReasonerStateInfoType> match = null;
		
		while (match == null) {
			String matchID = generateMatchID(game, Long.toString(number));
			try {
				match = createMatch(matchID, game, startclock, playclock, rolesToPlayerInfos, tournamentID, startTime, scrambled, weight, user);
			} catch (DuplicateInstanceException e) {
				logger.info("duplicate MatchID: " + matchID + " -> creating new one");
				number++;
			}
		}
		
		return match;
	}

	/**
	 * Creates a match with some default values: a generated match ID and start time "now".
	 */
	public NewMatch<TermType, ReasonerStateInfoType> createMatch(GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game,
			int startclock, int playclock, Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> rolesToPlayerInfos, 
			String tournamentID, boolean scrambled, double weight, User user) throws SQLException {
		return createMatch(game, startclock, playclock, rolesToPlayerInfos, tournamentID, new Date(), scrambled, weight, user);
	}

	/**
	 * Creates a match with some default values: a generated match ID, start time "now", no scrambling and a weight of 1.0.
	 * @param user 
	 */
	public NewMatch<TermType, ReasonerStateInfoType> createMatch(GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game,
			int startclock, int playclock, Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> rolesToPlayerInfos, 
			String tournamentID, User user) throws SQLException {
		return createMatch(game, startclock, playclock, rolesToPlayerInfos, tournamentID, new Date(), false, 1.0, user);
	}

	public ServerMatch<TermType, ReasonerStateInfoType> getMatch(String matchID)
			throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		PreparedStatement ps_roles = null;
		ResultSet rs = null;

		ServerMatch<TermType, ReasonerStateInfoType> result = null;

		try {
			ps = con.prepareStatement("SELECT `game`, `start_clock`, `play_clock`, `start_time`, `status`, `scrambled`, `tournament_id`, `weight`, `owner` FROM `matches` WHERE `match_id` = ? ;");
			ps.setString(1, matchID);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				Game<TermType, ReasonerStateInfoType> game = getGame(rs.getString("game"));
				int startclock = rs.getInt("start_clock");
				int playclock = rs.getInt("play_clock");
				Timestamp startTime = rs.getTimestamp("start_time");
				String status = rs.getString("status");
				boolean scrambled = rs.getBoolean("scrambled");
				String tournamentID = rs.getString("tournament_id");
				double weight = rs.getDouble("weight");
				User owner = getUser(rs.getString("owner"));

				ps_roles = con.prepareStatement("SELECT `player` , `roleindex` , `goal_value` FROM `match_players` WHERE `match_id` = ? ;");
				ps_roles.setString(1, matchID);

				ResultSet rs_roles = ps_roles.executeQuery();

				Map<RoleInterface<TermType>, PlayerInfo> rolesToPlayerInfos = new HashMap<RoleInterface<TermType>, PlayerInfo>();				
				Map<RoleInterface<TermType>, Integer> goalValues = new HashMap<RoleInterface<TermType>, Integer>();
				
				while (rs_roles.next()) {
					int roleindex = rs_roles.getInt("roleindex");
					RoleInterface<TermType> role = game.getOrderedRoles().get(roleindex);
					PlayerInfo playerInfo = getPlayerInfo(rs_roles.getString("player"));
					playerInfo.setRoleindex(roleindex);
						
					rolesToPlayerInfos.put(role, playerInfo);
					goalValues.put(role, rs_roles.getInt("goal_value"));
				}				

//				List<List<GameControllerErrorMessage>> errorMessages = getErrorMessages(matchID, getNumberOfXMLStates(matchID));
//				List<List<String>> jointMovesStrings = getJointMovesStrings(matchID);
				
				if (status.equals(ServerMatch.STATUS_NEW)) {
					result = new NewMatch<TermType, ReasonerStateInfoType>(
							matchID, game, startclock, playclock,
							rolesToPlayerInfos, startTime, scrambled, tournamentID, weight, owner, this);
				} else if (status.equals(ServerMatch.STATUS_SCHEDULED)) {
					result = new ScheduledMatch<TermType, ReasonerStateInfoType>(
							matchID, game, startclock, playclock,
							rolesToPlayerInfos, startTime, scrambled, tournamentID, weight, owner, this);
				} else if (status.equals(ServerMatch.STATUS_RUNNING)) {
					GameScramblerInterface gameScrambler;
					
					if (scrambled) {
						gameScrambler = new GameScrambler(getWordlistStream());
					} else {
						gameScrambler = identityGameScrambler;
					}
					result = new RunningMatch<TermType, ReasonerStateInfoType>(
							matchID, game, startclock, playclock,
							rolesToPlayerInfos, startTime, scrambled, tournamentID, weight, owner, this,
							getMoveFactory(), gameScrambler);
				} else if (status.equals(ServerMatch.STATUS_ABORTED)) {
					result = new AbortedMatch<TermType, ReasonerStateInfoType>(
							matchID, game, startclock, playclock,
							rolesToPlayerInfos, startTime, scrambled, tournamentID, weight, owner, this);
				} else if (status.equals(ServerMatch.STATUS_FINISHED)) {
					result = new FinishedMatch<TermType, ReasonerStateInfoType>(
							matchID, game, startclock, playclock,
							rolesToPlayerInfos, startTime, scrambled, tournamentID, weight, owner, this, goalValues);
				}
				
				if (result == null) {
					throw new SQLException("Field \"status\" in database entry for match " + matchID + " has an illegal value: " + status);
				}
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (ps_roles != null)
				try {ps_roles.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		// logger.info("String - Returning new match: " + matchID); //$NON-NLS-1$
		return result;
	}
	
	public NewMatch<TermType, ReasonerStateInfoType> getNewMatch(String matchID) throws SQLException {
		ServerMatch<TermType, ReasonerStateInfoType> match = getMatch(matchID);
		if (match instanceof NewMatch) {
			return (NewMatch<TermType, ReasonerStateInfoType>) match;
		}
		throw new IllegalArgumentException("Not a new match: " + matchID);
	}

	public ScheduledMatch<TermType, ReasonerStateInfoType> getScheduledMatch(String matchID) throws SQLException {
		ServerMatch<TermType, ReasonerStateInfoType> match = getMatch(matchID);
		if (match instanceof ScheduledMatch) {
			return (ScheduledMatch<TermType, ReasonerStateInfoType>) match;
		}
		throw new IllegalArgumentException("Not a scheduled match: " + matchID);
	}
	
	public RunningMatch<TermType, ReasonerStateInfoType> getRunningMatch(String matchID) throws SQLException {
		ServerMatch<TermType, ReasonerStateInfoType> match = getMatch(matchID);
		if (match instanceof RunningMatch) {
			return (RunningMatch<TermType, ReasonerStateInfoType>) match;
		}
		throw new IllegalArgumentException("Not a running match: " + matchID);
	}
	
	public FinishedMatch<TermType, ReasonerStateInfoType> getFinishedMatch(String matchID) throws SQLException {
		ServerMatch<TermType, ReasonerStateInfoType> match = getMatch(matchID);
		if (match instanceof FinishedMatch) {
			return (FinishedMatch<TermType, ReasonerStateInfoType>) match;
		}
		throw new IllegalArgumentException("Not a finished match: " + matchID);
	}
	
	public AbortedMatch<TermType, ReasonerStateInfoType> getAbortedMatch(String matchID) throws SQLException {
		ServerMatch<TermType, ReasonerStateInfoType> match = getMatch(matchID);
		if (match instanceof AbortedMatch) {
			return (AbortedMatch<TermType, ReasonerStateInfoType>) match;
		}
		throw new IllegalArgumentException("Not an aborted match: " + matchID);
	}
	
	private List<String> getAllMatchesFromTournament(Connection con, String tournamentID) throws SQLException {
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<String> result = new LinkedList<String>();
		
		try {
			ps = con.prepareStatement("SELECT DISTINCT `match_id` FROM `matches` WHERE `tournament_id` = ?;");
			ps.setString(1, tournamentID);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(rs.getString("match_id"));
			}
		} finally { 
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 
		
		return result;
	}

	public void deleteTournament(String tournamentID) throws SQLException {
		Connection con = getConnection();
		List<String> matchIDs = getAllMatchesFromTournament(con, tournamentID);
		
		for (String matchID : matchIDs) {
			deleteMatchWithDBConnection(matchID, con);
		}
		
		PreparedStatement ps = null;
		try {
			con.setAutoCommit(false);
			
			// LOW_PRIORITY means that the actual deletion will only 
			// be performed after there are no more reading clients. 
			ps = con.prepareStatement("DELETE LOW_PRIORITY FROM `tournaments` WHERE `tournament_id` = ? LIMIT 1");
			ps.setString(1, tournamentID);
			ps.executeUpdate();
			ps.close();
			
			con.commit();
		} catch (SQLException e) {
			if (con != null) {
				con.rollback();
			}
			throw e;
		} finally { 
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 
		
	}
	
	public void deleteMatch(String matchID) throws SQLException {
		Connection con = getConnection();
		deleteMatchWithDBConnection(matchID, con);
		if (con != null)
			try {con.close();} catch (SQLException e) {}
	}
	
	private void deleteMatchWithDBConnection(String matchID, Connection con) throws SQLException {
		PreparedStatement ps = null;
		try {
			con.setAutoCommit(false);
			
			// LOW_PRIORITY means that the actual deletion will only 
			// be performed after there are no more reading clients. 
			ps = con.prepareStatement("DELETE LOW_PRIORITY FROM `matches` WHERE `match_id` = ? LIMIT 1");
			ps.setString(1, matchID);
			ps.executeUpdate();
			ps.close();
			
			ps = con.prepareStatement("DELETE LOW_PRIORITY FROM `errormessages` WHERE `match_id` = ?");
			ps.setString(1, matchID);
			ps.executeUpdate();
			ps.close();
			
			ps = con.prepareStatement("DELETE LOW_PRIORITY FROM `match_players` WHERE `match_id` = ?");
			ps.setString(1, matchID);
			ps.executeUpdate();
			ps.close();
			
			ps = con.prepareStatement("DELETE LOW_PRIORITY FROM `states` WHERE `match_id` = ?");
			ps.setString(1, matchID);
			ps.executeUpdate();
			ps.close();
			
			ps = con.prepareStatement("DELETE LOW_PRIORITY FROM `moves` WHERE `match_id` = ?");
			ps.setString(1, matchID);
			ps.executeUpdate();
			ps.close();
			
			con.commit();
		} catch (SQLException e) {
			if (con != null) {
				con.rollback();
			}
			throw e;
		} finally { 
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 
	}
	
	public int getRowCount(String tableName) throws SQLException {
		Connection con = getConnection();
		Statement statement = null;
		ResultSet rs = null;

		try {
			statement = con.createStatement();
			statement.execute("SELECT COUNT( * ) FROM `" + tableName + "` ;");
			rs = statement.getResultSet();
			
			if (rs.next()) {
				return rs.getInt(1);				
			} 
			throw new SQLException("Something went wrong.");
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (statement != null)
				try {statement.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

	}
	
	public List<Game<?, ?>> getGames(int startRow, int numDisplayedRows) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<Game<?, ?>> result = new LinkedList<Game<?, ?>>();
		
		try {
			ps = con.prepareStatement("SELECT `name` FROM `games` ORDER BY `name` LIMIT ? , ? ;");
			ps.setInt(1, startRow);
			ps.setInt(2, numDisplayedRows);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getGame(rs.getString("name")));
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}
	
	public List<Game<TermType, ReasonerStateInfoType>> getAllEnabledGames() throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<Game<TermType,ReasonerStateInfoType>> result = new LinkedList<Game<TermType,ReasonerStateInfoType>>();
		
		try {
			ps = con.prepareStatement("SELECT `name` FROM `games` WHERE `enabled`=TRUE ORDER BY `name` ;");
			rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getGame(rs.getString("name")));
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}

	public List<User> getUsers(int startRow, int numDisplayedRows) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<User> result = new LinkedList<User>();
		
		try {
			ps = con.prepareStatement("SELECT `user_name` FROM `users` ORDER BY `user_name` LIMIT ? , ? ;");
			ps.setInt(1, startRow);
			ps.setInt(2, numDisplayedRows);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getUser(rs.getString("user_name")));
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}
	
	public List<PlayerInfo> getPlayerInfos(int startRow, int numDisplayedRows) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<PlayerInfo> result = new LinkedList<PlayerInfo>();
		
		try {
			ps = con.prepareStatement("SELECT `name` FROM `players` ORDER BY `name` LIMIT ? , ? ;");
			ps.setInt(1, startRow);
			ps.setInt(2, numDisplayedRows);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getPlayerInfo(rs.getString("name")));
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}
	
	public List<RemotePlayerInfo> getPlayerInfos() throws SQLException {
		return getPlayerInfos(null);
	}
		
	public List<RemotePlayerInfo> getPlayerInfos(String status) throws SQLException {
		return getPlayerInfos(status, null, null, null);
	}

	public List<RemotePlayerInfo> getPlayerInfos(String status, Boolean availableForRoundRobinMatches, Boolean availableForManualMatches, String owner) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<RemotePlayerInfo> result = new LinkedList<RemotePlayerInfo>();
		// it is important that a new list is returned here,
		// since the result of this method will be shuffled in
		// AbstractRoundRobinScheduler.createPlayerInfos()
		
		String select  = "SELECT `name`";
		String from    = "FROM `players`";
		String where   = "WHERE TRUE";
		String orderBy = "ORDER BY `name`";

		try {
			List<Object> parameters = new LinkedList<Object>();

			// add specific filters
			if (status != null) {
				where += " AND `status` = ?";
				parameters.add(status);
			}
			if (availableForRoundRobinMatches != null) {
				where += " AND `plays_round_robin` = ?";
				parameters.add(availableForRoundRobinMatches);
			}
			if (availableForManualMatches != null) {
				where += " AND `plays_manual` = ?";
				parameters.add(availableForManualMatches);
			}
			if (owner != null) {
				where += " AND `owner` = ?";
				parameters.add(owner);
			}
			
			// prepare statement, fill in parameters
			ps = con.prepareStatement(select + " " + from + " " + where + " " + orderBy + ";");
			for (int i = 0; i < parameters.size(); i++) {
				Object parameter = parameters.get(i);
				if (parameter instanceof Integer) {
					ps.setInt(i + 1, (Integer) parameter);
				} else if (parameter instanceof String) {
					ps.setString(i + 1, (String) parameter);
				} else if (parameter instanceof Boolean) {
					ps.setBoolean(i + 1, (Boolean) parameter);
				} else {
					throw new InternalError("this should never happen");
				}
			}
			rs = ps.executeQuery();
			
			while (rs.next()) {
				PlayerInfo playerInfo = getPlayerInfo(rs.getString("name"));
				if (playerInfo instanceof RemotePlayerInfo) {
					result.add((RemotePlayerInfo) playerInfo);
				}
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}
	
	public List<RemotePlayerInfo> getPlayerInfosForUser(String userName) throws SQLException {
		return getPlayerInfos(null, null, null, userName);
		
	}
	
	/**
	 * @param startRow
	 *            The first database row to be returned
	 * @param numDisplayedRows
	 *            The number of rows (= matches) to be returned
	 * @param playerName
	 *            If not <code>null</code>, return only matches from this player
	 * @param gameName
	 *            If not <code>null</code>, return only matches of this game
	 * @param tournamentID
	 *            If not <code>null</code>, return only matches from this tournament
	 * @param owner
	 *            If not <code>null</code>, return only matches owned by owner
	 * @param excludeNew
	 *            If <code>true</code>, do not return matches with status "new"
	 * @return All matches that match the given filter criteria. Filters can be
	 *         combined.
	 * @throws SQLException
	 */
	public List<ServerMatch<TermType, ReasonerStateInfoType>> getMatches(
			int startRow, int numDisplayedRows, String playerName,
			String gameName, String tournamentID, String owner, boolean excludeNew)
			throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<ServerMatch<TermType, ReasonerStateInfoType>> result = new LinkedList<ServerMatch<TermType, ReasonerStateInfoType>>();
		
		try {
			ps = prepareMatchesStatement(con, false, startRow, numDisplayedRows, playerName, gameName, tournamentID, owner, excludeNew);
			
			rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getMatch(rs.getString("match_id")));
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}

	public int getRowCountMatches(String playerName, String gameName, String tournamentID, String owner, boolean excludeNew) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = prepareMatchesStatement(con, true, 0, Integer.MAX_VALUE, playerName, gameName, tournamentID, owner, excludeNew);
			
			rs = ps.executeQuery();
			
			if (rs.next()) {
				return rs.getInt(1);				
			} 
			throw new SQLException("Something went wrong.");
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 
	}

	/**
	 * 
	 * @return a word list stream to use for scrambling
	 */
	private InputStream getWordlistStream() {
		return AbstractDBConnector.class.getResourceAsStream("/wordlist.txt");
	}

	/**
	 * @param onlyRowCount
	 *            If <code>true</code>, prepare a statement to count the
	 *            number of rows
	 * @param owner 
	 */
	private PreparedStatement prepareMatchesStatement(Connection con, boolean onlyRowCount, int startRow, int numDisplayedRows, String playerName, String gameName, String tournamentID, String owner, boolean excludeNew) throws SQLException, InternalError {
		PreparedStatement ps = null;

		// initialize statement and parameters
		String select;
		if (onlyRowCount) {
			select =  "SELECT COUNT(*)";
		} else {
			select =  "SELECT `m`.`match_id`";
		}
		String from =          "FROM `matches` AS `m`";
		String where =         "WHERE TRUE";
		String orderBy =       "ORDER BY `m`.`start_time`";
		final String limit =   "LIMIT ? , ?";

		List<Object> parameters = new LinkedList<Object>();

		// add specific filters
		if (gameName != null) {
			where += " AND `m`.`game` = ?";
			parameters.add(gameName);
		}
		if (playerName != null) {
			from += ", `match_players` AS `p`";
			where += " AND `m`.`match_id` = `p`.`match_id` AND `p`.`player` = ?";
			parameters.add(playerName);
		}
		if (tournamentID != null) {
			where += " AND `m`.`tournament_id` = ?";
			parameters.add(tournamentID);
		}
		if (owner != null) {
			where += " AND `m`.`owner` = ?";
			parameters.add(owner);
		}
		if (excludeNew) {
			where += " AND `m`.`status` != '" + ServerMatch.STATUS_NEW + "'";
			where += " AND `m`.`status` != '" + ServerMatch.STATUS_SCHEDULED + "'";
		}else{
			// show the new matches last
			orderBy = "ORDER BY CASE"
						+ " WHEN `m`.`status`!='" + ServerMatch.STATUS_NEW + "' AND `m`.`status`!='" + ServerMatch.STATUS_SCHEDULED + "'"
						+ " THEN 1 ELSE 2 END, `m`.`start_time`";
		}
		parameters.add(startRow);
		parameters.add(numDisplayedRows);
		
		// prepare statement, fill in parameters
		ps = con.prepareStatement(select + " " + from + " " + where + " " + orderBy + " " + limit + ";");
		for (int i = 0; i < parameters.size(); i++) {
			Object parameter = parameters.get(i);
			if (parameter instanceof Integer) {
				ps.setInt(i + 1, (Integer) parameter);
			} else if (parameter instanceof String) {
				ps.setString(i + 1, (String) parameter);
			} else {
				throw new InternalError("this should never happen");
			}
		}
		return ps;
	}
	
	
	public void setMatchStatus(String matchID, String status) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		
		try {
			
			String setStartTime = "";
			if(status.equals(ServerMatch.STATUS_RUNNING)) {
				setStartTime = ", `start_time` = CURRENT_TIMESTAMP";				
			}
			ps = con.prepareStatement("UPDATE `ggpserver`.`matches` SET `status` = ? " + setStartTime + " WHERE `matches`.`match_id` = ? LIMIT 1 ;");
			ps.setString(1, status);
			ps.setString(2, matchID);
			ps.executeUpdate();
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 
	}
	
	public void setMatchGoalValues(ServerMatch<TermType, ReasonerStateInfoType> match, Map<? extends RoleInterface<?>, Integer> goalValues) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;

		try {
			
			List<? extends RoleInterface<?>> orderedRoles = match.getGame().getOrderedRoles();
			
			ps = con.prepareStatement("UPDATE `ggpserver`.`match_players` SET `goal_value` = ? WHERE `match_players`.`match_id` = ? AND `match_players`.`roleindex` = ? LIMIT 1 ;");
			
			for (int roleIndex = 0; roleIndex < orderedRoles.size(); roleIndex++) {
				ps.setInt(1, goalValues.get(orderedRoles.get(roleIndex)));
				ps.setString(2, match.getMatchID());
				ps.setInt(3, roleIndex);
				ps.executeUpdate();
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		}
	}
	
	public void setMatchWeight(String matchID, double weight) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		
		try {
			ps = con.prepareStatement("UPDATE `matches` SET `weight` = ? WHERE `matches`.`match_id` = ? LIMIT 1 ;");
			ps.setDouble(1, weight);
			ps.setString(2, matchID);
			
			ps.executeUpdate();
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		}
	}

	public void setMatchStartclock(String matchID, int startclock) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("UPDATE `matches` SET `start_clock` = ? WHERE `matches`.`match_id` = ? LIMIT 1 ;");
			ps.setInt(1, startclock);
			ps.setString(2, matchID);
			
			ps.executeUpdate();
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		}
	}

	public void setMatchPlayclock(String matchID, int playclock) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("UPDATE `matches` SET `play_clock` = ? WHERE `matches`.`match_id` = ? LIMIT 1 ;");
			ps.setInt(1, playclock);
			ps.setString(2, matchID);
			
			ps.executeUpdate();
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		}
	}

	public void setMatchPlayerInfo(String matchID, int roleIndex,
			PlayerInfo playerInfo) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;

		try {
//			ps = con.prepareStatement("INSERT INTO `match_players` (`match_id`, `player`, `roleindex`) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE `player` = ? ;");
			ps = con.prepareStatement("UPDATE `match_players` SET `player` = ? WHERE `match_id` = ? AND `roleindex` = ? ;");

			ps.setString(1, playerInfo.getName());
			ps.setString(2, matchID);
			ps.setInt(3, roleIndex);
			ps.executeUpdate();
		} finally {
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		}
	}

	public void setMatchGame(NewMatch<TermType, ReasonerStateInfoType> match, GameInterface<TermType, State<TermType, ReasonerStateInfoType>> newGame) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		
		try {
			String oldMatchID = match.getMatchID();
			
			// updateMatchID
			String newMatchID = updateMatchID(oldMatchID, newGame);
			
			// delete old player infos
			// we cannot just UPDATE, but must DELETE and then INSERT, since the
			// number of roles between old + new game can differ.
			ps = con.prepareStatement("DELETE LOW_PRIORITY FROM `match_players` WHERE `match_id` = ?");
			ps.setString(1, oldMatchID);
			ps.executeUpdate();
			ps.close();
			
			// store new player infos
			ps = con.prepareStatement("INSERT INTO `match_players` (`match_id`, `player`, `roleindex`) VALUES (?, ?, ?) ;");
			ps.setString(1, newMatchID);
			
			List<PlayerInfo> newPlayerInfos = makeNewPlayerInfos(match.getOrderedPlayerInfos(), newGame);
			int roleIndex = 0;
			for (PlayerInfo playerInfo : newPlayerInfos) {
				ps.setString(2, playerInfo.getName());
				ps.setInt(3, roleIndex);
				ps.executeUpdate();
				
				roleIndex++;
			}
			ps.close();

			ps = con.prepareStatement("UPDATE `matches` SET `game` = ? WHERE `match_id` = ? LIMIT 1 ;");
		
			ps.setString(1, newGame.getName());
			ps.setString(2, newMatchID);
			ps.executeUpdate();

		} finally {
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		}
		
	}

	public void setMatchScrambled(String matchID, boolean scrambled) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("UPDATE `matches` SET `scrambled` = ? WHERE `matches`.`match_id` = ? LIMIT 1 ;");
			ps.setBoolean(1, scrambled);
			ps.setString(2, matchID);
			
			ps.executeUpdate();
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		}
	}

	/**
	 * Updates a match with a generated match ID.
	 * @throws SQLException 
	 */
	private String updateMatchID(String oldMatchID,
			GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game) throws SQLException {
		
		long number = System.currentTimeMillis();
		
		while (true) {
			String newMatchID = generateMatchID(game, Long.toString(number));
			try {
				updateMatchID(oldMatchID, newMatchID);
				return newMatchID;
			} catch (DuplicateInstanceException e) {
				number++;
			}
		}
	}	

	private void updateMatchID(String oldMatchID, String newMatchID) throws DuplicateInstanceException, SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement("UPDATE `matches` SET `match_id` = ? WHERE `match_id` = ? LIMIT 1 ;");

			ps.setString(1, newMatchID);
			ps.setString(2, oldMatchID);
			ps.executeUpdate();
			ps.close();
			
			// Strictly speaking, the following would be necessary, but is already done by setMatchGame().
			// also, we cannot just UPDATE, but must DELETE + INSERT (see setMatchGame()).
			
//			ps = con.prepareStatement("UPDATE `match_players` SET `match_id` = ? WHERE `match_id` = ? ;");
//
//			ps.setString(1, newMatchID);
//			ps.setString(2, oldMatchID);
//			ps.executeUpdate();
//			ps.close();

			// The following updates are unnecessary, because updateMatchID must
			// only be called on "new" matches, which don't have states,
			// errormessages or moves.
			//			ps = con.prepareStatement("UPDATE `errormessages` SET `match_id` = ? WHERE `match_id` = ?;");
//
//			ps.setString(1, newMatchID);
//			ps.setString(2, oldMatchID);
//			ps.executeUpdate();
//			ps.close();
//			
//			ps = con.prepareStatement("UPDATE `states` SET `match_id` = ? WHERE `match_id` = ? ;");
//
//			ps.setString(1, newMatchID);
//			ps.setString(2, oldMatchID);
//			ps.executeUpdate();
//			ps.close();
//			
//			ps = con.prepareStatement("UPDATE `moves` SET `match_id` = ? WHERE `match_id` = ? ;");
//
//			ps.setString(1, newMatchID);
//			ps.setString(2, oldMatchID);
//			ps.executeUpdate();
//			ps.close();
			
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		}
	}

	private List<PlayerInfo> makeNewPlayerInfos(
			List<? extends PlayerInfo> oldPlayerInfos,
			GameInterface<TermType, State<TermType, ReasonerStateInfoType>> newGame) {
		List<PlayerInfo> newPlayerInfos; 
		
		if (oldPlayerInfos.size() >= newGame.getNumberOfRoles()) {
			// more players than roles --> truncate
			newPlayerInfos = new LinkedList<PlayerInfo>(oldPlayerInfos.subList(0, newGame.getNumberOfRoles()));
		} else {
			// less players than roles --> add random players
			newPlayerInfos = new LinkedList<PlayerInfo>(oldPlayerInfos);
			while (newPlayerInfos.size() < newGame.getNumberOfRoles()) {
				newPlayerInfos.add(new RandomPlayerInfo(newPlayerInfos.size() - 1));
			}
		}
		
		return newPlayerInfos;
	}

	/**
	 * @param stepNumber counting starts from 1
	 */
	public void addErrorMessage(String matchID, int stepNumber, GameControllerErrorMessage errorMessage) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;

		assert (stepNumber > 0);

		try {
			ps = con.prepareStatement("INSERT INTO `errormessages` (`match_id` , `step_number`, `type`, `message`, `player`) VALUES (?, ?, ?, ?, ?);");
			ps.setString(1, matchID);
			ps.setInt(2, stepNumber);
			ps.setString(3, errorMessage.getType());
			ps.setString(4, errorMessage.getMessage());
			ps.setString(5, errorMessage.getPlayerName());
			
			ps.executeUpdate();
//		this shouldn't happen because we use a generated primary key:		
//		} catch (MySQLIntegrityConstraintViolationException e) {
//			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
//			throw new DuplicateInstanceException(e);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 
	}

	/**
	 * @param stepNumber counting starts from 1
	 */
	public void addJointMove(String matchID, int stepNumber, JointMoveInterface<? extends TermInterface> jointMove) throws SQLException, DuplicateInstanceException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		
		assert (stepNumber > 0);

		try {

			int roleindex = 0;
			for (MoveInterface<? extends TermInterface> move : jointMove.getOrderedMoves()) {
				ps = con.prepareStatement("INSERT INTO `moves` (`match_id` , `step_number`, `roleindex`, `move`) VALUES (?, ?, ?, ?);");
				ps.setString(1, matchID);
				ps.setInt(2, stepNumber);
				ps.setInt(3, roleindex);
				ps.setString(4, move.getTerm().getKIFForm());
				
				ps.executeUpdate();
				roleindex++;
			}
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 
	}

	/**
	 * @param stepNumber counting starts from 1
	 */
	public void addState(String matchID, int stepNumber, String xmlState) throws SQLException, DuplicateInstanceException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		
		assert (stepNumber > 0);

		try {
			ps = con.prepareStatement("INSERT INTO `states` (`match_id` , `step_number`, `state`) VALUES (?, ?, ?);");
			ps.setString(1, matchID);
			ps.setInt(2, stepNumber);
			ps.setString(3, xmlState);
			
			ps.executeUpdate();
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 
	}
	
	/**
	 * Returns a list of lists of error messages for each step number. The first
	 * element of result is the list of error messages for step number 1, and so
	 * on. Unfortunately, step number counting starts with 1, so to get the
	 * error messages for step number i, use result.get(i - 1).
	 */
	public List<List<GameControllerErrorMessage>> getAllErrorMessages(String matchID, int numberOfStates) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<List<GameControllerErrorMessage>> result = new ArrayList<List<GameControllerErrorMessage>>(numberOfStates);
		
		for (int i = 0; i < numberOfStates; i++) {
			result.add(new LinkedList<GameControllerErrorMessage>());
		}
		
		try {
			ps = con.prepareStatement("SELECT `step_number`, `type`, `message`, `player` FROM `errormessages` WHERE `match_id` = ? ORDER BY `step_number` ;");
			ps.setString(1, matchID);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				int errorMsgStepNumber = rs.getInt("step_number");   // step number counting starts with 1 
				assert (errorMsgStepNumber > 0);

				if (errorMsgStepNumber > numberOfStates) {
					String message = "errorMsgStepNumber bigger than numberOfStates! Causing match id: " + matchID;
					logger.severe(message);        //$NON-NLS-1$s
					throw new InternalError(message);					
				}
				GameControllerErrorMessage errorMessage = new GameControllerErrorMessage(rs.getString("type"), rs.getString("message"), rs.getString("player"));
				result.get(errorMsgStepNumber - 1).add(errorMessage);
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}
	
	
	
	/**
	 * @param stepNumber starts from 1
	 */
	public List<GameControllerErrorMessage> getErrorMessages(String matchID, int stepNumber) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		assert (stepNumber > 0);

		List<GameControllerErrorMessage> result = new ArrayList<GameControllerErrorMessage>();
		
		try {
			ps = con.prepareStatement("SELECT `type`, `message`, `player` FROM `errormessages` WHERE `match_id` = ? AND `step_number` = ? ORDER BY `player` ;");
			ps.setString(1, matchID);
			ps.setInt(2, stepNumber);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				GameControllerErrorMessage errorMessage = new GameControllerErrorMessage(rs.getString("type"), rs.getString("message"), rs.getString("player"));
				result.add(errorMessage);
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}


	public List<List<String>> getJointMovesStrings(String matchID) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<List<String>> result = new LinkedList<List<String>>();
		
		try {
			ps = con.prepareStatement("SELECT `step_number`, `roleindex`, `move` FROM `moves` WHERE `match_id` = ? ORDER BY `step_number`, `roleindex` ;");
			ps.setString(1, matchID);
			rs = ps.executeQuery();
			
			List<String> jointMove = null;
			int stepNumber = 0;
			while (rs.next()) {
				int roleIndex = rs.getInt("roleindex");
				if (roleIndex == 0) {
					stepNumber++;
					if (jointMove != null) {
						result.add(jointMove);
					}
					jointMove = new LinkedList<String>();
				}
				assert(jointMove != null);
				assert (stepNumber > 0);
				assert(rs.getInt("step_number") == stepNumber);
				assert(roleIndex == jointMove.size());
				jointMove.add(rs.getString("move"));
			}
			if (jointMove != null) {
				result.add(jointMove);
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}
	
	public List<String> getJointMove(String matchID, int stepNumber) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<String> result = new LinkedList<String>();
		
		assert (stepNumber > 0);

		try {
			ps = con.prepareStatement("SELECT `roleindex`, `move` FROM `moves` WHERE `match_id` = ? AND `step_number` = ? ORDER BY `roleindex` ;");
			ps.setString(1, matchID);
			ps.setInt(2, stepNumber);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				int roleIndex = rs.getInt("roleindex");  // roleindex starts from 0 in DB

				result.add(rs.getString("move"));

				assert(roleIndex == result.size() - 1);
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}

	public List<String> getXMLStates(String matchID) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<String> result = new LinkedList<String>();

		try {
			ps = con.prepareStatement("SELECT `step_number`, `state` FROM `states` WHERE `match_id` = ? ORDER BY `step_number` ;");
			ps.setString(1, matchID);
			rs = ps.executeQuery();
			
			int stepNumber = 1;
			while (rs.next()) {
				assert(rs.getInt("step_number") == stepNumber);
				result.add(rs.getString("state"));
				stepNumber++;
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		}

		return result;
	}

	public String getXMLState(String matchID, int stepNumber) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		assert (stepNumber > 0);

		try {
			ps = con.prepareStatement("SELECT `state` FROM `states` WHERE `match_id` = ? AND `step_number` = ? ;");
			ps.setString(1, matchID);
			ps.setInt(2, stepNumber);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				return rs.getString("state");
			} else {
				throw new SQLException("XML state not found!");
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		}
	}

	public int getNumberOfXMLStates(String matchID) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement("SELECT COUNT(*) FROM `states` WHERE `match_id` = ? ;");
			ps.setString(1, matchID);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				return rs.getInt(1);
			} else {
				throw new SQLException("XML state not found!");
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		}
	}

	public void updatePlayerInfo(String playerName, String host, int port, User user, String status, boolean availableForRoundRobinMatches, boolean availableForManualMatches) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;

		try { 
			ps = con.prepareStatement("UPDATE `ggpserver`.`players` "
							+ "SET `host` = ?, `port` = ?, `owner` = ?, `status` = ?, `plays_round_robin` = ?, `plays_manual` = ? "
							+ "WHERE `name` = ? LIMIT 1 ;");
			ps.setString(1, host);
			ps.setInt(2, port);
			ps.setString(3, user.getUserName());
			ps.setString(4, status);
			ps.setBoolean(5, availableForRoundRobinMatches);
			ps.setBoolean(6, availableForManualMatches);
			ps.setString(7, playerName);
			ps.executeUpdate();
			
			RemotePlayerInfo playerInfo = (RemotePlayerInfo) getPlayerInfo(playerName);
			notifyPlayerStatusListeners(playerInfo);			
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 
	}

	public void updateGameInfo(String gameName, String gameDescription, String stylesheet, boolean enabled) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;

		try { 
			ps = con.prepareStatement("UPDATE `ggpserver`.`games` "
							+ "SET `gamedescription` = ?, `stylesheet` = ?, `enabled` = ? "
							+ "WHERE `name` = ? LIMIT 1 ;");
			ps.setString(1, gameDescription);
			ps.setString(2, stylesheet);
			ps.setBoolean(3, enabled);
			ps.setString(4, gameName);
			ps.executeUpdate(); 
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		}
	}

	public Game<TermType, ReasonerStateInfoType> getNextPlayedGame() throws SQLException {
		String nextGameName = getConfigEntry(ConfigOption.NEXT_PLAYED_GAME.getDBKey());
		if (nextGameName == null) {
			return null;
		}
		return getGame(nextGameName);
	}
	
	public void setNextPlayedGame(Game<?, ?> nextPlayedGame) throws SQLException {
		storeConfigEntry(ConfigOption.NEXT_PLAYED_GAME.getDBKey(), nextPlayedGame.getName());
	}


	/**
	 * Generic config to store key/value pairs (Strings up to length 255). Used
	 * for things that do not occur often enough to justify having a table of
	 * their own.
	 */
	private String getConfigEntry(String key) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		String result = null;
		try {
			ps = con.prepareStatement("SELECT `value` FROM `config` WHERE `key` = ? ;");
			ps.setString(1, key);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				result  = rs.getString("value");
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 
		return result;
	}

	public void setConfigOption(ConfigOption option, String value) throws SQLException {
		storeConfigEntry(option.getDBKey(), value);
	}

	public String getConfigOption(ConfigOption option) throws SQLException {
		String value = getConfigEntry(option.getDBKey());
		if(value == null){
			value = option.getDefaultValue();
			setConfigOption(option, value);
		}
		return value;
	}

	private synchronized void storeConfigEntry(String key, String value) throws SQLException {
		if (getConfigEntry(key) == null) {
			// key doesn't exist --> create it
			try {
				createConfigEntry(key, value);
			} catch (DuplicateInstanceException e) {
				throw new InternalError("This should never happen!" + e);
				// this should never happen, because we checked explicitly for non-existence before, so the key cannot exist!
			}
		} else {
			// key exists --> update
			updateConfigEntry(key, value);
		}
		
	}

	private void createConfigEntry(String key, String value) throws SQLException, DuplicateInstanceException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		try { 

			ps = con.prepareStatement("INSERT INTO `config` (`key` , `value`) VALUES (?, ?);");
			ps.setString(1, key);
			ps.setString(2, value);
			
			ps.executeUpdate();
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 
	}
	
	private void updateConfigEntry(String key, String value) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("UPDATE `ggpserver`.`config` "
							+ "SET `value` = ? "
							+ "WHERE `config`.`key` = ? LIMIT 1 ;");
			ps.setString(1, value);
			ps.setString(2, key);
			ps.executeUpdate(); 
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		}
	}
	
	public void addPlayerStatusListener(PlayerStatusListener listener) {
		playerStatusListeners.add(listener);
	}
	
	private static Connection getConnection() throws SQLException {
		if (datasource == null) {
			try {
				datasource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver");
			} catch (NamingException e) {
				logger.severe("exception: " + e); //$NON-NLS-1$
				throw new InternalError("Could not lookup datasource!");
			} 
		}
		return datasource.getConnection();
	}
	
	public static String generateMatchID(GameInterface<?, ?> game, String appendix) {
		String firstPart;
		if (game == null) {
			firstPart = "null";
		} else {
			firstPart = game.getName();
		}
		
		String secondPart;
		if (appendix == null) {
			secondPart = ".?????????????";
		} else {
			secondPart = "." + appendix;
		}
		
		if (firstPart.length() + secondPart.length() > MATCHID_MAXLENGTH) {
			firstPart = firstPart.substring(0, MATCHID_MAXLENGTH - secondPart.length() - 2) + "..";
		}
		return firstPart + secondPart;
	}

	public List<Tournament<TermType , ReasonerStateInfoType>> getTournaments() throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<Tournament<TermType , ReasonerStateInfoType>> result = new LinkedList<Tournament<TermType , ReasonerStateInfoType>>();
		
		try {
			ps = con.prepareStatement("SELECT `tournament_id` FROM `tournaments` ORDER BY `tournament_id` ;");
			rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getTournament(rs.getString("tournament_id")));
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}
	
	public List<Tournament<TermType , ReasonerStateInfoType>> getTournamentsCreatedByUser(String userName) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<Tournament<TermType , ReasonerStateInfoType>> result = new LinkedList<Tournament<TermType , ReasonerStateInfoType>>();
		
		try {
			ps = con.prepareStatement("SELECT `tournament_id` FROM `tournaments` WHERE `owner` = ? ORDER BY `tournament_id` ;");
			ps.setString(1, userName);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getTournament(rs.getString("tournament_id")));
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}

	public List<Tournament<TermType , ReasonerStateInfoType>> getTournamentsForPlayer(String playerName) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		List<Tournament<TermType , ReasonerStateInfoType>> result = new LinkedList<Tournament<TermType , ReasonerStateInfoType>>();
		
		try {
			ps = con.prepareStatement("SELECT DISTINCT `matches`.`tournament_id` AS `tournament_id` FROM `match_players` LEFT JOIN `matches` ON `match_players`.`match_id` = `matches`.`match_id` WHERE `match_players`.`player` = ? AND (`matches`.`status`='finished' OR `matches`.`status`='running');");
			ps.setString(1, playerName);
			rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getTournament(rs.getString("tournament_id")));
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}

	public Tournament<TermType , ReasonerStateInfoType> getTournament(String tournamentID) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		Tournament<TermType , ReasonerStateInfoType> result = null;
		
		try { 
			ps = con.prepareStatement("SELECT `tournament_id`, `owner` FROM `tournaments` WHERE `tournament_id` = ?;");
			ps.setString(1, tournamentID);
			rs = ps.executeQuery();
			
			if (rs.next()) {
				result = new Tournament<TermType, ReasonerStateInfoType>(rs.getString("tournament_id"), getUser(rs.getString("owner")), this);
			}
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 

		return result;
	}
	
	public TournamentStatistics<TermType, ReasonerStateInfoType> getTournamentStatistics(String tournamentID) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			ps = con.prepareStatement(
					"SELECT `match_players`.`player`, SUM(`match_players`.`goal_value` * `matches`.`weight`) AS the_sum, COUNT(`match_players`.`goal_value`) AS the_count " +
					"FROM `matches`, `match_players` " +
					"WHERE `matches`.`match_id` = `match_players`.`match_id` AND `matches`.`tournament_id` = ? AND `match_players`.`goal_value` IS NOT NULL AND `player` != '" + PLAYER_RANDOM + "' AND `player` != '" + PLAYER_LEGAL + "' " +
					"GROUP BY `match_players`.`player` " +
					"ORDER BY the_sum DESC");
			ps.setString(1, tournamentID);
			rs = ps.executeQuery();
			Map<PlayerInfo, Integer> totalReward = new HashMap<PlayerInfo, Integer>();
			Map<PlayerInfo, Integer> numberOfMatches = new HashMap<PlayerInfo, Integer>();
			while (rs.next()) {
				PlayerInfo player = getPlayerInfo(rs.getString("player"));
				totalReward.put(player, rs.getInt("the_sum"));
				numberOfMatches.put(player, rs.getInt("the_count"));
			}
			return new TournamentStatistics<TermType, ReasonerStateInfoType>(tournamentID, numberOfMatches, totalReward);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 
	}

	public GameStatistics<TermType, ReasonerStateInfoType> getGameStatistics(String gameName) throws SQLException {
		Game<TermType, ReasonerStateInfoType> game = getGame(gameName);
		HashMap<RoleInterface<TermType>, GamePlayerStatistics<TermType,ReasonerStateInfoType>> gamePlayerStatisticsPerRole = new HashMap<RoleInterface<TermType>, GamePlayerStatistics<TermType,ReasonerStateInfoType>>();
		int roleIndex = 0;
		for(RoleInterface<TermType> role:game.getOrderedRoles()){
			gamePlayerStatisticsPerRole.put(role, getGamePlayerStatistics(game, roleIndex));
			roleIndex++;
		}
		return new GameStatistics<TermType, ReasonerStateInfoType>(getGameRoleStatistics(game), getGamePlayerStatistics(game), gamePlayerStatisticsPerRole);
	}

	public GameRoleStatistics<TermType, ReasonerStateInfoType> getGameRoleStatistics(Game<TermType, ReasonerStateInfoType> game) throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			
			Map<RoleInterface<TermType>, PerformanceInformation> informationPerRole = new HashMap<RoleInterface<TermType>, PerformanceInformation>();
			ps = con.prepareStatement(
					"SELECT" +
					"	`match_players`.`roleindex`," +
					"	AVG( `match_players`.`goal_value` ) AS `avg`," +
					"	STDDEV_POP( `match_players`.`goal_value` ) AS `std_dev`," +
					"	COUNT( `match_players`.`goal_value` ) AS `count`" +
					"FROM" +
					"		`match_players`" +
					"	INNER JOIN" +
					"		`matches`" +
					"	USING ( `match_id` )" +
					"WHERE" +
					"	NOT ISNULL( `match_players`.`goal_value` )" +
					"	AND `matches`.`game` = ?" +
					"GROUP BY" +
					"	`match_players`.`roleindex`");
			ps.setString(1, game.getName());
			rs = ps.executeQuery();
			while (rs.next()) {
				informationPerRole.put(game.getRole(rs.getInt("roleindex")), new PerformanceInformation(rs.getInt("count"), rs.getDouble("avg"), rs.getDouble("std_dev")));
			}

			return new GameRoleStatistics<TermType, ReasonerStateInfoType>(game, informationPerRole);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 
	}

	public GamePlayerStatistics<TermType, ReasonerStateInfoType> getGamePlayerStatistics(Game<TermType, ReasonerStateInfoType> game) throws SQLException {
		return getGamePlayerStatistics(game, -1);
	}

	public GamePlayerStatistics<TermType, ReasonerStateInfoType> getGamePlayerStatistics(Game<TermType, ReasonerStateInfoType> game, int roleIndex) throws SQLException  {

		Connection con = getConnection();
		PreparedStatement ps = null;
		ResultSet rs = null;

		try {
			
			Map<PlayerInfo, PerformanceInformation> informationPerPlayer = new HashMap<PlayerInfo, PerformanceInformation>();
			String roleSelector = "";
			if (roleIndex>=0) {
				roleSelector = " AND `match_players`.`roleindex` = ? ";
			}
			ps = con.prepareStatement(
					"SELECT" +
					"	`match_players`.`player`," +
					"	AVG( `match_players`.`goal_value` ) AS `avg`," +
					"	STDDEV_POP( `match_players`.`goal_value` ) AS `std_dev`," +
					"	COUNT( `match_players`.`goal_value` ) AS `count`" +
					"FROM" +
					"		`match_players`" +
					"	INNER JOIN" +
					"		`matches`" +
					"	USING ( `match_id` )" +
					"WHERE" +
					"	NOT ISNULL( `match_players`.`goal_value` )" +
					"	AND `matches`.`game` = ?" +
						roleSelector +
					"GROUP BY" +
					"	`match_players`.`player`");
			ps.setString(1, game.getName());
			if (roleIndex>=0) {
				ps.setInt(2, roleIndex);
			}
			rs = ps.executeQuery();
			while (rs.next()) {
				PlayerInfo player = getPlayerInfo(rs.getString("player"));
				informationPerPlayer.put(player, new PerformanceInformation(rs.getInt("count"), rs.getDouble("avg"), rs.getDouble("std_dev")));
			}
			RoleInterface<TermType> role = null;
			if(roleIndex>=0)
				role = game.getRole(roleIndex);
			return new GamePlayerStatistics<TermType, ReasonerStateInfoType>(game, role, informationPerPlayer);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
			if (rs != null)
				try {rs.close();} catch (SQLException e) {}
		} 
	}

	public Tournament<TermType , ReasonerStateInfoType> createTournament(String tournamentID, User owner) throws DuplicateInstanceException,
			SQLException {
		
		Connection con = getConnection();
		PreparedStatement ps = null;
		try {

			ps = con.prepareStatement("INSERT INTO `tournaments` (`tournament_id`, `owner`) VALUES (?, ?) ;");
			ps.setString(1, tournamentID);
			ps.setString(2, owner.getUserName());
			
			ps.executeUpdate();
			ps.close();
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		} 

		logger.info("Creating new tournament: " + tournamentID); //$NON-NLS-1$
		return new Tournament<TermType, ReasonerStateInfoType>(tournamentID, owner, this);
	}
	
	/**
	 * Cleans up all matches marked as "running" in the database (sets their status to "aborted").
	 * Also changes "scheduled" matches back to "new" (or aborts them if they belong to the "round_robin_tournament").
	 * @throws SQLException 
	 */
	public void cleanup() throws SQLException {
		Connection con = getConnection();
		PreparedStatement ps = null;

		try {
			ps = con.prepareStatement("UPDATE `matches` SET `status` = ? WHERE `status` = ? ;");
			ps.setString(1, ServerMatch.STATUS_ABORTED);
			ps.setString(2, ServerMatch.STATUS_RUNNING);
			ps.executeUpdate(); 

			ps = con.prepareStatement("UPDATE `matches` SET `status` = ? WHERE `status` = ? AND tournament_id != '" + Tournament.ROUND_ROBIN_TOURNAMENT_ID + "' ;");
			ps.setString(1, ServerMatch.STATUS_NEW);
			ps.setString(2, ServerMatch.STATUS_SCHEDULED);
			ps.executeUpdate(); 

			ps = con.prepareStatement("UPDATE `matches` SET `status` = ? WHERE `status` = ? AND tournament_id = '" + Tournament.ROUND_ROBIN_TOURNAMENT_ID + "' ;");
			ps.setString(1, ServerMatch.STATUS_ABORTED);
			ps.setString(2, ServerMatch.STATUS_SCHEDULED);
			ps.executeUpdate(); 
		} finally { 
			if (con != null)
				try {con.close();} catch (SQLException e) {}
			if (ps != null)
				try {ps.close();} catch (SQLException e) {}
		}
	}

	public User getAdminUser() throws SQLException {
		return getUser("admin");
	}

}
