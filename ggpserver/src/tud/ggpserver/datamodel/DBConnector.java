package tud.ggpserver.datamodel;

import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.LegalPlayerInfo;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.gamecontroller.scrambling.GameScramblerInterface;
import tud.gamecontroller.scrambling.IdentityGameScrambler;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.AbstractReasonerFactory;
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
public class DBConnector<TermType extends TermInterface, ReasonerStateInfoType> {
	public static final int MATCHID_MAXLENGTH = 40;

//	private final AbstractReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory;
	private final static GameScramblerInterface gamescrambler;
	
	static {
		gamescrambler = new IdentityGameScrambler();    // we don't do any scrambling (yet)
	}

	public User createUser(String userName, String password) throws NamingException, DuplicateInstanceException, SQLException {
		Connection con = null;

		try { 
//			String hashedPass = RealmBase.Digest(password, "SHA-1", null);
			String hashedPass = Digester.digest(password, "SHA-1");
			
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 

			PreparedStatement ps = con.prepareStatement("INSERT INTO `users` (`user_name`, `user_pass`) VALUES (?, ?);");
			ps.setString(1, userName);
			ps.setString(2, hashedPass);
			ps.executeUpdate(); 

			ps = con.prepareStatement("INSERT INTO `user_roles` (`user_name`, `role_name`) VALUES (?, 'member');");
			ps.setString(1, userName);
			ps.executeUpdate();
			
		} catch (NoSuchAlgorithmException e) {
			throw new InternalError("java.security.MessageDigest supports SHA-1, so we shouldn't ever get this error!?");
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} 
		finally { 
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
		
		return new User(userName);
	}

	public User getUser(String userName) throws NamingException, SQLException {
		Connection con = null;
		User result = null;

		try { 
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 
			
			PreparedStatement ps = con.prepareStatement("SELECT `user_name` FROM `users` WHERE `user_name` = ?;");
			ps.setString(1, userName);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				result = new User(userName);
			} else {
				result = null;
			}
		}
		finally { 
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
		return result;
	}
	
	public RemotePlayerInfo createPlayerInfo(String name, String host,
			int port, User owner, String status) throws NamingException,
			DuplicateInstanceException, SQLException {
		
		assert(!name.equals("Legal"));
		assert(!name.equals("Random"));

		Connection con = null;
		try { 
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 

			PreparedStatement ps = con.prepareStatement("INSERT INTO `players` (`name` , `host` , `port` , `owner`, `status`) VALUES (?, ?, ?, ?, ?);");
			ps.setString(1, name);
			ps.setString(2, host);
			ps.setInt(3, port);
			ps.setString(4, owner.getUserName());
			ps.setString(5, status);
			
			ps.executeUpdate();
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} 
		finally { 
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
		
		return new RemotePlayerInfo(name, host, port, owner, status);
	}
	
	public PlayerInfo getPlayerInfo(String name) throws NamingException, SQLException {
		if (name.equals("Legal")) {
			return new LegalPlayerInfo(-1);
		} else if (name.equals("Random")) {
			return new RandomPlayerInfo(-1);
		}
		
		Connection con = null;

		RemotePlayerInfo result = null;
		
		try { 
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 
			
			PreparedStatement ps = con.prepareStatement("SELECT `host` , `port` , `owner` , `status` FROM `players` WHERE `name` = ?;");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				result = new RemotePlayerInfo(name, rs.getString("host"), rs.getInt("port"), getUser(rs.getString("owner")), rs.getString("status"));
			} else {
				result = null;
			}
		}
		finally { 
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
		return result;
	}

	public Game<TermType, ReasonerStateInfoType> createGame(String gameDescription,
			String name, AbstractReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory) throws NamingException, DuplicateInstanceException,
			SQLException {
		
		Connection con = null;
		try {
			DataSource ds = (DataSource) new InitialContext()
					.lookup("java:comp/env/jdbc/ggpserver");
			con = ds.getConnection();

			PreparedStatement ps = con
					.prepareStatement("INSERT INTO `games` (`name` , `gamedescription`) VALUES (?, ?);");
			ps.setString(1, name);
			ps.setString(2, gameDescription);
			
			ps.executeUpdate();
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		return new Game<TermType, ReasonerStateInfoType>(gameDescription, name, reasonerFactory.getReasoner(gameDescription, name));
	}

	public Game<TermType, ReasonerStateInfoType> getGame(String name, AbstractReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory) throws NamingException, SQLException {
		Connection con = null;

		Game<TermType,ReasonerStateInfoType> result = null;
		
		try { 
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 
			
			PreparedStatement ps = con.prepareStatement("SELECT `gamedescription` FROM `games` WHERE `name` = ?;");
			ps.setString(1, name);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				String gameDescription = rs.getString("gamedescription");
				result = new Game<TermType, ReasonerStateInfoType>(gameDescription , name, reasonerFactory.getReasoner(gameDescription, name));
				
			} else {
				result = null;
			}
		}
		finally {
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
		return result;
	}
	
	public Match<TermType, ReasonerStateInfoType> createMatch(String matchID, Game<TermType, ReasonerStateInfoType> game,
			int startclock, int playclock, Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> playerinfos,
			Date startTime, AbstractReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory) throws NamingException, DuplicateInstanceException,
			SQLException {
		
		Connection con = null;
		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver");
			con = ds.getConnection();

			PreparedStatement ps = con
					.prepareStatement("INSERT INTO `matches` (`match_id` , `game` , `start_clock` , `play_clock` , `start_time`) VALUES (?, ?, ?, ?, ?);");
			ps.setString(1, matchID);
			ps.setString(2, game.getName());
			ps.setInt(3, startclock);
			ps.setInt(4, playclock);
			ps.setTimestamp(5, new Timestamp(startTime.getTime()));
			
			ps.executeUpdate();
			
			// players
			ps = con.prepareStatement("INSERT INTO `match_players` (`match_id` , `player` , `roleindex`) VALUES (?, ?, ?);");
			
			ps.setString(1, matchID);
			
			Integer roleindex = 0;
			for (RoleInterface<TermType> role : game.getOrderedRoles()) {
				ps.setString(2, playerinfos.get(role).getName());
				ps.setString(3, roleindex.toString());
		
				ps.executeUpdate();
				roleindex++;
			}
			
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}

		return new Match<TermType, ReasonerStateInfoType>(matchID, game, startclock, playclock,
				playerinfos, startTime, reasonerFactory.getMoveFactory(), gamescrambler);
	}
	
	/**
	 * Creates a match with a generated match ID.
	 */
	public Match<TermType, ReasonerStateInfoType> createMatch(Game<TermType, ReasonerStateInfoType> game,
			int startclock, int playclock, Map<? extends RoleInterface<TermType>, ? extends PlayerInfo> playerinfos,
			Date startTime, AbstractReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory) throws NamingException, SQLException {
		
		long number = System.currentTimeMillis();
		
		Match<TermType, ReasonerStateInfoType> match = null;
		
		while (match == null) {
			String firstPart = "Match." + game.getName();
			String secondPart = "." + Long.toString(number);
			if (firstPart.length() + secondPart.length() > MATCHID_MAXLENGTH) {
				firstPart = firstPart.substring(0, MATCHID_MAXLENGTH - secondPart.length() - 2) + "..";
			}
			String matchID = firstPart + secondPart;
			try {
				match = createMatch(matchID, game, startclock, playclock, playerinfos, startTime, reasonerFactory);
			} catch (DuplicateInstanceException e) {
				number++;
			}
		}
		
		return match;
	}	

	public Match<TermType, ReasonerStateInfoType> getMatch(String matchID, AbstractReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory)
			throws NamingException, SQLException {
		Connection con = null;

		Match<TermType, ReasonerStateInfoType> result = null;

		try {
			DataSource ds = (DataSource) new InitialContext()
					.lookup("java:comp/env/jdbc/ggpserver");
			con = ds.getConnection();

			PreparedStatement ps = con
					.prepareStatement("SELECT `game` , `start_clock` , `play_clock` , `start_time` , `status` FROM `matches` WHERE `match_id` = ?;");
			ps.setString(1, matchID);
			ResultSet rs = ps.executeQuery();
			
			if (rs.next()) {
				Game<TermType, ReasonerStateInfoType> game = getGame(rs.getString("game"), reasonerFactory);
				String status = rs.getString("status");
				
				PreparedStatement ps_roles = con
				.prepareStatement("SELECT `player` , `roleindex` , `goal_value` FROM `match_players` WHERE `match_id` = ?;");
				ps_roles.setString(1, matchID);

				ResultSet rs_roles = ps_roles.executeQuery();

				Map<RoleInterface<TermType>, PlayerInfo> playerinfos = new HashMap<RoleInterface<TermType>, PlayerInfo>();				
				Map<RoleInterface<TermType>, Integer> goalValues = new HashMap<RoleInterface<TermType>, Integer>();
				
				while (rs_roles.next()) {
					int roleindex = rs_roles.getInt("roleindex");
					RoleInterface<TermType> role = game.getOrderedRoles().get(roleindex);
					PlayerInfo playerInfo = getPlayerInfo(rs_roles.getString("player"));
					playerInfo.setRoleindex(roleindex);
						
					playerinfos.put(role, playerInfo);
					goalValues.put(role, rs_roles.getInt("goal_value"));
				}				
				
				result = new Match<TermType, ReasonerStateInfoType>(matchID, game, rs.getInt("start_clock"), 
						rs.getInt("play_clock"), playerinfos, rs.getTimestamp("start_time"), reasonerFactory.getMoveFactory(), gamescrambler);
				result.setStatus(status);
				if (status.equals(Match.STATUS_FINISHED)) {
					result.setGoalValues(goalValues);
				}
			} else {
				result = null;
			}
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
		List<String> states = getStates(matchID);
		result.setXmlStates(states);
		result.setErrorMessages(getErrorMessages(matchID, states.size()));
		result.setJointMovesStrings(getJointMovesStrings(matchID));
		return result;
	}
	
	public int getRowCount(String tableName) throws NamingException, SQLException {
		Connection con = null;

		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 

			Statement statement = con.createStatement();
			statement.execute("SELECT COUNT( * ) FROM `" + tableName + "`;");
			ResultSet rs = statement.getResultSet();
			
			
			if (rs.next()) {
				return rs.getInt(1);				
			} else {
				throw new SQLException("Something went wrong.");
			}
		}
		finally {
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
	}
	
	public int getRowCountPlayerMatches(String playerName) throws NamingException, SQLException {
		Connection con = null;

		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 

			PreparedStatement ps = con.prepareStatement("SELECT COUNT( `m`.`match_id` ) "
					+ "FROM `matches` AS `m`, `match_players` AS `p` " 
					+ "WHERE `m`.`match_id` = `p`.`match_id` AND `player` = ? ;");
			ps.setString(1, playerName);
			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				return rs.getInt(1);				
			} else {
				throw new SQLException("Something went wrong.");
			}
		}
		finally {
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
	}
	
	
	public List<Game<TermType, ReasonerStateInfoType>> getGames(int startRow, int numDisplayedRows, AbstractReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory) throws NamingException, SQLException {
		Connection con = null;

		List<Game<TermType,ReasonerStateInfoType>> result = new LinkedList<Game<TermType,ReasonerStateInfoType>>();
		
		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 
			
			PreparedStatement ps = con.prepareStatement("SELECT `name` FROM `games` LIMIT ? , ?;");
			ps.setInt(1, startRow);
			ps.setInt(2, numDisplayedRows);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getGame(rs.getString("name"), reasonerFactory));
			}
		}
		finally {
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
		return result;
	}
	
	public List<Game<TermType, ReasonerStateInfoType>> getAllGames(AbstractReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory) throws NamingException, SQLException {
		Connection con = null;

		List<Game<TermType,ReasonerStateInfoType>> result = new LinkedList<Game<TermType,ReasonerStateInfoType>>();
		
		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 
			
			PreparedStatement ps = con.prepareStatement("SELECT `name` FROM `games`;");
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getGame(rs.getString("name"), reasonerFactory));
			}
		}
		finally {
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
		return result;
	}
	
	public List<User> getUsers(int startRow, int numDisplayedRows) throws NamingException, SQLException {
		Connection con = null;

		List<User> result = new LinkedList<User>();
		
		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 
			
			PreparedStatement ps = con.prepareStatement("SELECT `user_name` FROM `users` LIMIT ? , ?;");
			ps.setInt(1, startRow);
			ps.setInt(2, numDisplayedRows);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getUser(rs.getString("user_name")));
			}
		}
		finally {
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
		return result;
	}
	
	public List<PlayerInfo> getPlayerInfos(int startRow, int numDisplayedRows) throws NamingException, SQLException {
		Connection con = null;

		List<PlayerInfo> result = new LinkedList<PlayerInfo>();
		
		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 
			
			PreparedStatement ps = con.prepareStatement("SELECT `name` FROM `players` LIMIT ? , ?;");
			ps.setInt(1, startRow);
			ps.setInt(2, numDisplayedRows);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getPlayerInfo(rs.getString("name")));
			}
		}
		finally {
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
		return result;
	}
	
	public List<PlayerInfo> getPlayerInfos(String status) throws NamingException, SQLException {
		Connection con = null;

		List<PlayerInfo> result = new LinkedList<PlayerInfo>();
			// it is important that only a COPY of a list is returned here,
			// since the result of this method will be shuffled in
			// AbstractRoundRobinScheduler.createPlayerInfos()
		
		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 
			
			PreparedStatement ps = con.prepareStatement("SELECT `name` FROM `players` where `status` = ?;");
			ps.setString(1, status);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getPlayerInfo(rs.getString("name")));
			}
		}
		finally {
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
		return result;
	}
	
	public List<RemotePlayerInfo> getPlayerInfosForUser(String userName) throws NamingException, SQLException {
		Connection con = null;

		List<RemotePlayerInfo> result = new LinkedList<RemotePlayerInfo>();
			// it is important that only a COPY of a list is returned here,
			// since the result of this method will be shuffled in
			// AbstractRoundRobinScheduler.createPlayerInfos()
		
		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 
			
			PreparedStatement ps = con.prepareStatement("SELECT `name` FROM `players` where `owner` = ?;");
			ps.setString(1, userName);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add((RemotePlayerInfo) getPlayerInfo(rs.getString("name")));
			}
		}
		finally {
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
		return result;
	}
	
	public List<Match<TermType, ReasonerStateInfoType>> getMatches(int startRow, int numDisplayedRows, AbstractReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory, String playerName) throws NamingException, SQLException {
		Connection con = null;

		List<Match<TermType, ReasonerStateInfoType>> result = new LinkedList<Match<TermType, ReasonerStateInfoType>>();
		
		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 
			
			PreparedStatement ps;
			if (playerName == null) {
				ps = con.prepareStatement("SELECT `match_id` FROM `matches` ORDER BY `start_time` LIMIT ? , ?;");
				ps.setInt(1, startRow);
				ps.setInt(2, numDisplayedRows);				
			} else {
				ps = con.prepareStatement("SELECT `m`.`match_id` "
					+ "FROM `matches` AS `m`, `match_players` AS `p` " 
					+ "WHERE `m`.`match_id` = `p`.`match_id` AND `player` = ? " 
					+ "ORDER BY `start_time` LIMIT ? , ?;");
				ps.setString(1, playerName);
				ps.setInt(2, startRow);
				ps.setInt(3, numDisplayedRows);			
			}
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				result.add(getMatch(rs.getString("match_id"), reasonerFactory));
			}
		}
		finally {
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
		return result;
	}
	
	public void setMatchStatus(Match<? extends TermType, ReasonerStateInfoType> match, String status) throws SQLException, NamingException {
		Connection con = null;

		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 
			
			PreparedStatement ps = con.prepareStatement("UPDATE `ggpserver`.`matches` SET `status` = ? WHERE `matches`.`match_id` = ? LIMIT 1 ;");
			ps.setString(1, status);
			ps.setString(2, match.getMatchID());
			ps.executeUpdate();
		}
		finally {
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
		
	}
	
	public void setMatchGoalValues(Match<? extends TermType, ReasonerStateInfoType> match, Map<? extends RoleInterface<?>, Integer> goalValues) throws SQLException, NamingException {
		Connection con = null;

		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 
			
			List<? extends RoleInterface<?>> orderedRoles = match.getGame().getOrderedRoles();
			
			for (int roleIndex = 0; roleIndex < orderedRoles.size(); roleIndex++) {
				PreparedStatement ps = con.prepareStatement("UPDATE `ggpserver`.`match_players` SET `goal_value` = ? WHERE `match_players`.`match_id` = ? AND `match_players`.`roleindex` = ? LIMIT 1 ;");
				
				ps.setInt(1, goalValues.get(orderedRoles.get(roleIndex)));
				ps.setString(2, match.getMatchID());
				ps.setInt(3, roleIndex);
				ps.executeUpdate();
			}
		}
		finally {
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
	}

	/**
	 * @param stepNumber counting starts from 1
	 */
	public void addErrorMessage(String matchID, int stepNumber, GameControllerErrorMessage errorMessage) throws SQLException, NamingException {
		Connection con = null;
		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver");
			con = ds.getConnection();

			PreparedStatement ps = con.prepareStatement("INSERT INTO `errormessages` (`match_id` , `step_number`, `type`, `message`) VALUES (?, ?, ?, ?);");
			ps.setString(1, matchID);
			ps.setInt(2, stepNumber);
			ps.setString(3, errorMessage.getType());
			ps.setString(4, errorMessage.getMessage());
			
			ps.executeUpdate();
//		this shouldn't happen because we use a generated primary key:		
//		} catch (MySQLIntegrityConstraintViolationException e) {
//			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
//			throw new DuplicateInstanceException(e);
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * @param stepNumber counting starts from 1
	 * @throws SQLException 
	 * @throws NamingException 
	 * @throws DuplicateInstanceException 
	 */
	public void addJointMove(String matchID, int stepNumber, JointMoveInterface<? extends TermInterface> jointMove) throws SQLException, NamingException, DuplicateInstanceException {
		Connection con = null;
		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver");
			con = ds.getConnection();

			int roleindex = 0;
			for (MoveInterface<? extends TermInterface> move : jointMove.getOrderedMoves()) {
				PreparedStatement ps = con.prepareStatement("INSERT INTO `moves` (`match_id` , `step_number`, `roleindex`, `move`) VALUES (?, ?, ?, ?);");
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
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}

	/**
	 * @param stepNumber counting starts from 1
	 * @throws SQLException 
	 * @throws NamingException 
	 * @throws DuplicateInstanceException 
	 */
	public void addState(String matchID, int stepNumber, String xmlState) throws SQLException, NamingException, DuplicateInstanceException {
		Connection con = null;
		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver");
			con = ds.getConnection();

			PreparedStatement ps = con.prepareStatement("INSERT INTO `states` (`match_id` , `step_number`, `state`) VALUES (?, ?, ?);");
			ps.setString(1, matchID);
			ps.setInt(2, stepNumber);
			ps.setString(3, xmlState);
			
			ps.executeUpdate();
		} catch (MySQLIntegrityConstraintViolationException e) {
			// MySQLIntegrityConstraintViolationException means here that the key could not be inserted
			throw new DuplicateInstanceException(e);
		} finally {
			if (con != null)
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		}
	}
	
	/**
	 * Returns a list of lists of error messages for each step number. The first
	 * element of result is the list of error messages for step number 1, and so
	 * on. Unfortunately, step number counting starts with 1, so to get the
	 * error messages for step number i, use result.get(i - 1).
	 */
	public List<List<GameControllerErrorMessage>> getErrorMessages(String matchID, int numberOfStates) throws SQLException, NamingException {
		Connection con = null;

		List<List<GameControllerErrorMessage>> result = new ArrayList<List<GameControllerErrorMessage>>(numberOfStates);
		
		for (int i = 0; i < numberOfStates; i++) {
			result.add(new LinkedList<GameControllerErrorMessage>());
		}
		
		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 
			
			PreparedStatement ps = con.prepareStatement("SELECT `step_number`, `type`, `message` FROM `errormessages` where `match_id` = ? ORDER BY `step_number`;");
			ps.setString(1, matchID);
			ResultSet rs = ps.executeQuery();
			
			while (rs.next()) {
				int errorMsgStepNumber = rs.getInt("step_number");   // step number counting starts with 1 
				GameControllerErrorMessage errorMessage = new GameControllerErrorMessage(rs.getString("type"), rs.getString("message"));
				result.get(errorMsgStepNumber - 1).add(errorMessage);
			}
		}
		finally {
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
		return result;
	}

	public List<List<String>> getJointMovesStrings(String matchID) throws SQLException, NamingException {
		Connection con = null;

		List<List<String>> result = new LinkedList<List<String>>();
		
		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 
			
			PreparedStatement ps = con.prepareStatement("SELECT `step_number`, `roleindex`, `move` FROM `moves` where `match_id` = ? ORDER BY `step_number`, `roleindex`;");
			ps.setString(1, matchID);
			ResultSet rs = ps.executeQuery();
			
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
				jointMove.add(rs.getString("move"));

				assert(rs.getInt("step_number") == stepNumber);
				assert(roleIndex == jointMove.size() - 1);
			}
			if (jointMove != null) {
				result.add(jointMove);
			}
		}
		finally {
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
		return result;
	}

	public List<String> getStates(String matchID) throws SQLException, NamingException {
		Connection con = null;

		List<String> result = new LinkedList<String>();
		
		try {
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection(); 
			
			PreparedStatement ps = con.prepareStatement("SELECT `step_number`, `state` FROM `states` where `match_id` = ? ORDER BY `step_number`;");
			ps.setString(1, matchID);
			ResultSet rs = ps.executeQuery();
			
			int stepNumber = 1;
			while (rs.next()) {
				assert(rs.getInt("step_number") == stepNumber);
				result.add(rs.getString("state"));
				stepNumber++;
			}
		}
		finally {
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
		return result;
	}

	public void updatePlayerInfo(String playerName, String host, int port, User user, String status) throws SQLException, NamingException {
		Connection con = null;

		try { 
			DataSource ds = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/ggpserver"); 
			con = ds.getConnection();

			PreparedStatement ps = con.prepareStatement("UPDATE `ggpserver`.`players` "
							+ "SET `host` = ?, `port` = ?, `owner` = ?, `status` = ? "
							+ "WHERE `players`.`name` = ? LIMIT 1 ;");
			ps.setString(1, host);
			ps.setInt(2, port);
			ps.setString(3, user.getUserName());
			ps.setString(4, status);
			ps.setString(5, playerName);
			ps.executeUpdate(); 
			
		} finally { 
			if ( con != null ) 
				try { con.close(); } catch ( SQLException e ) { e.printStackTrace(); } 
		} 
	}




	
// /**
// * Similar to PHP's mysql_real_escape_string(). Escapes the following
//	 * characters using a backslash ('\'):
//	 * 
//	 * Backslash:
//	 * \\   : backslash
//	 * 
//	 * These can all terminate a quoted value:
//	 * \x00 : null-byte 
//	 * \n   : newline
//	 * \r   : carriage return
//	 * '    : single quote
//	 * "    : double quote
//	 * \x1a : ^Z, DOS end-of-file
//	 *
//	 * These are also replaced for some reason in some implementations:
//	 * \b   : backspace
//	 * \t   : tab
//	 * 
//	 * These are used as wildcards in "LIKE" String comparisons:
//	 * _    : underscore
//	 * %    : percentage
//	 * 
//	 * All of this is specific to MySQL, because some other databases do the 
//	 * escaping differently.
//	 * 
//	 * Alternatively, one can simply use PreparedStatements, in which case 
//	 * the JDBC driver will escape all data for us, for the specific database 
//	 * that we're using. EXCEPT _ and % in LIKE-Clauses.
//	 * 
//	 */
//	private String mySqlRealEscapeString(String unescaped) {
//		// The backslash must be handled first, otherwise the other escape sequences would be affected
//		return unescaped.replace("\\", "\\\\").replace("\u0000", "\\0")
//				.replace("\n", "\\n").replace("\r", "\\r").replace("'", "\\'")
//				.replace("\"", "\\\"").replace("\u001A", "\\Z").replace("\b",
//						"\\b").replace("\t", "\\t").replace("%", "\\%")
//				.replace("_", "\\_");
//	}
//	

}
