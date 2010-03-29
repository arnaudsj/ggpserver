/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de>
                  2009-2010 Stephan Schiffel <stephan.schiffel@gmx.de>
                  2010 Nicolas JEAN <njean42@gmail.com>

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

package tud.ggpserver.formhandlers;

import static tud.ggpserver.datamodel.DBConnectorFactory.getDBConnector;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;

import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.players.LegalPlayerInfo;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.Game;
import tud.ggpserver.datamodel.RemoteOrHumanPlayerInfo;
import tud.ggpserver.datamodel.RemotePlayerInfo;
import tud.ggpserver.datamodel.Tournament;
import tud.ggpserver.datamodel.User;
import tud.ggpserver.datamodel.matches.NewMatch;
import tud.ggpserver.datamodel.matches.RunningMatch;
import tud.ggpserver.datamodel.matches.ScheduledMatch;
import tud.ggpserver.datamodel.matches.ServerMatch;
import tud.ggpserver.scheduler.MatchRunner;
import tud.ggpserver.util.Utilities;

public class EditTournament extends ShowMatches {
	public static final String ADD_MATCH = "add_match";
	public static final String START_MATCH = "start_match";
	public static final String ABORT_MATCH = "abort_match";
	public static final String DELETE_MATCH = "delete_match";
	public static final String CLONE_MATCH = "clone_match";
	
	private static final Logger logger = Logger.getLogger(EditTournament.class.getName());
	
	private Tournament<?, ?> tournament = null;
	private String action = null;
	private ServerMatch<?, ?> match = null;
	private boolean correctlyPerformed = false;
	private User user = null;
	private HashMap<GDLVersion, List<PlayerInfoForEditTournament>> playerInfos = null;
	
	private String errorString = "No error";

	public List<? extends Game<?, ?>> getGames() throws SQLException {
		return db.getAllEnabledGames();
	}
	
	public String getErrorString() {
		return errorString;
	}

	public void setMatchID(String matchID) throws SQLException {
		this.match = db.getMatch(matchID);
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public Tournament<?, ?> getTournament() {
		return tournament;
	}

	@Override
	public void setTournamentID(String tournamentID) throws SQLException {
		tournament = db.getTournament(tournamentID);
		super.setTournamentID(tournamentID);
	}

	@Override
	public String getTargetJsp() {
		// tournament could be null if it was not correctly set. This should
		// only happen if the user fiddled manually with the GET parameters, so
		// he deserves the NullPointerException that he or she will get.
		return "edit_tournament.jsp?tournamentID=" + tournament.getTournamentID();
	}

	@Override
	protected boolean excludeNewMatches() {
		return false;
	}
	
	/**
	 * 
	 * @return true if the user set with setUser is allowed to edit the tournament
	 * @throws SQLException
	 */
	public boolean isAllow() throws SQLException {
		if (tournament == null || user == null) {
			errorString = "tournament or user not set";
			logger.warning(errorString);
			return false;
		}
		
		if (tournament.getOwner().equals(user))
			return true;
		
		if (user.isAdmin())
			return true;
		
		if (Tournament.MANUAL_TOURNAMENT_ID.equals(tournament.getTournamentID()))
			return true;
		
		errorString = "user '"+user.getUserName()+"' is not allowed to edit tournament '"+tournament.getTournamentID()+"'";
		
		logger.warning(errorString);
		return false;
	}

	private boolean checkMatchOwner() {
		if (!match.getOwner().equals(user) && !user.isAdmin()) {
			errorString = "match '" + match.getMatchID() + "' is not owned by '" + user.getUserName() + "'";
			return false;
		} else {
			return true;
		}
	}

	public boolean isValid() throws SQLException {
		if (!isAllow())
			return false;
		
		errorString = null;
		if (action.equals(ADD_MATCH)) {
		} else if (match == null) {
			errorString = "match does not exist";
		} else if (action.equals(START_MATCH)) {
			if (!(match instanceof NewMatch)) {
				errorString = "match '" + match.getMatchID() + "' is not new (status: '" + match.getStatus() + "')";
			}else if (checkMatchOwner()) {
				// check that all players are active and TODO: compatible gdl
				
				// There might still be a race condition such that a match is scheduled but never run (e.g., if a player goes offline
				// right after this check), but we avoid most cases here. 
				for(PlayerInfo playerInfo:match.getPlayerInfos()) {
					if(playerInfo instanceof RemotePlayerInfo) {
						RemotePlayerInfo remotePlayerInfo = (RemotePlayerInfo)playerInfo;
						if(!remotePlayerInfo.getStatus().equals(RemoteOrHumanPlayerInfo.STATUS_ACTIVE)) {
							errorString = "start match is invalid: "+remotePlayerInfo.getName()+" is " + remotePlayerInfo.getStatus() + "(not " + RemoteOrHumanPlayerInfo.STATUS_ACTIVE + ")";
							break;
						}
						if(!remotePlayerInfo.isAvailableForManualMatches() && !remotePlayerInfo.getOwner().equals(user)) {
							errorString = "start match is invalid: "+remotePlayerInfo.getName()+" is not available and not owned by "+user.getUserName();
							break;
						}
					}
				}
			}
		} else if (action.equals(ABORT_MATCH)) {
			if (!(match instanceof ScheduledMatch) && !(match instanceof RunningMatch)) {
				errorString = "match '" + match.getMatchID() + "' can not be aborted. It is neither scheduled nor running (status: '" + match.getStatus() + "').";
			}else {
				checkMatchOwner();
			}
		} else if (action.equals(DELETE_MATCH)) {
			checkMatchOwner();
		} else if (action.equals(CLONE_MATCH)) {
		} else {
			errorString = "invalid action: '" + action + "'";
		}
		if (errorString != null) {
			logger.warning(errorString);
			return false;
		}else{
			return true;
		}
	}
	
	@SuppressWarnings("unchecked")
	public void performAction() throws SQLException {
		if (!isValid()) {
			throw new IllegalStateException("performAction() called without checking isValid() first!");
		}
		if (action.equals(ADD_MATCH)) {
			addMatch();
		} else if (action.equals(START_MATCH)) {
			assert (match instanceof NewMatch);       // checked in isValid()
			startMatch((NewMatch) match);
		} else if (action.equals(ABORT_MATCH)) {
			assert (match instanceof RunningMatch || match instanceof ScheduledMatch);   // checked in isValid()
			abortMatch(match);
		} else if (action.equals(DELETE_MATCH)) {
			deleteMatch(match);
		} else {
			assert (action.equals(CLONE_MATCH));
			cloneMatch(match);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void addMatch() throws SQLException {
		List<? extends Game<?, ?>> allEnabledGames = db.getAllEnabledGames();
		Game<?,?> game = allEnabledGames.get(0);
		Map<RoleInterface, PlayerInfo> rolesToPlayerInfos = new HashMap<RoleInterface, PlayerInfo>();
		
		int roleIndex = 0;
		for (RoleInterface<?> role : game.getOrderedRoles()) {
			rolesToPlayerInfos.put(role, new RandomPlayerInfo(roleIndex, game.getGdlVersion()));
			roleIndex++;
		}
		
		((AbstractDBConnector)db).createMatch(game, Tournament.DEFAULT_STARTCLOCK,
				Tournament.DEFAULT_PLAYCLOCK, rolesToPlayerInfos, tournament.getTournamentID(), user);

		correctlyPerformed = true;
	}

	@SuppressWarnings("unchecked")
	private void startMatch(NewMatch match) throws SQLException {
		MatchRunner.getInstance().scheduleMatch(match);
		correctlyPerformed = true;
	}

	@SuppressWarnings("unchecked")
	private void abortMatch(ServerMatch match) throws SQLException {
		MatchRunner.getInstance().delete(match); // abort the match, if it is scheduled or running
		correctlyPerformed = true;
	}

	@SuppressWarnings("unchecked")
	private void deleteMatch(ServerMatch match) throws SQLException {
		MatchRunner.getInstance().delete(match); // abort the match, if it is scheduled or running
		db.deleteMatch(match.getMatchID());
		correctlyPerformed = true;
	}

	@SuppressWarnings("unchecked")
	private void cloneMatch(ServerMatch match) throws SQLException {
		db.createMatch(match.getGame(), match.getStartclock(), match.getPlayclock(), 
				match.getRolesToPlayerInfos(), tournament.getTournamentID(), match.isScrambled(), match.getWeight(), user);
		correctlyPerformed = true;
	}

	public boolean isCorrectlyPerformed() {
		if (!correctlyPerformed) {
			errorString = "There occurred an error while performing the action'" + action + "' for match '"+match.getMatchID() + "'";
		}
		return correctlyPerformed;
	}
	
	/* [old and unused]
	 * public List<PlayerInfo> getEnabledPlayerInfos() throws SQLException {
		List<PlayerInfo> result = new LinkedList<PlayerInfo>();
		result.add(new RandomPlayerInfo(-1,this.match.getGame().getGdlVersion()));
		result.add(new LegalPlayerInfo(-1,this.match.getGame().getGdlVersion()));
		result.addAll(db.getPlayerInfos(RemotePlayerInfo.STATUS_ACTIVE));

		return result;
	}*/

	public Map<GDLVersion, List<PlayerInfoForEditTournament>> getPlayerInfos() throws SQLException {
		
		if(playerInfos == null) {
			playerInfos = new HashMap<GDLVersion, List<PlayerInfoForEditTournament>>();
			
			for (GDLVersion gdlVersion: GDLVersion.values()) {
				
				playerInfos.put(gdlVersion, new LinkedList<PlayerInfoForEditTournament>());
				
				User admin=db.getAdminUser();
				// add local players
				playerInfos.get(gdlVersion).add(new PlayerInfoForEditTournament(new RandomPlayerInfo(-1,gdlVersion).getName(), true, admin));
				playerInfos.get(gdlVersion).add(new PlayerInfoForEditTournament(new LegalPlayerInfo(-1,gdlVersion).getName(), true, admin));
				List<? extends RemotePlayerInfo> allRemotePlayers = db.getPlayerInfos();
				// add all available players (that comply with the given GDL Version)
				ListIterator<? extends RemotePlayerInfo> i = allRemotePlayers.listIterator();
				while(i.hasNext()){
					RemotePlayerInfo p = i.next();
					if(p.getStatus().equals(RemoteOrHumanPlayerInfo.STATUS_ACTIVE) &&
						(p.isAvailableForManualMatches() || p.getOwner().equals(user)) &&
						Utilities.areCompatible(p, gdlVersion)) { // compatibility check: only allow players to play games they understand!
							playerInfos.get(gdlVersion).add(new PlayerInfoForEditTournament(p.getName(), true, p.getOwner()));
							i.remove();
					}
				}
				// add all remaining players (but mark them as unavailable)
				i = allRemotePlayers.listIterator();
				while(i.hasNext()){
					RemotePlayerInfo p = i.next();
					playerInfos.get(gdlVersion).add(new PlayerInfoForEditTournament(p.getName(), false, p.getOwner()));
				}
				
			}
				
		}
		
		return playerInfos;
		
	}
	
	public String shortMatchID(ServerMatch<?, ?> match) {
		String matchID = match.getMatchID();
		String gameName = match.getGame().getName();
		if(matchID.startsWith(gameName)) {
			matchID = matchID.substring(gameName.length()+1);
		}
		return matchID;
	}
	
	public void setUserName(String userName) throws SQLException {
		user = getDBConnector().getUser(userName);
		if (!user.isAdmin())
			super.setOwner(user.getUserName()); // normal users should only see their own matches for editing
	}
	
	public String getUserName() {
		return user.getUserName();
	}
	
	public boolean isGoalEditable() {
		if (Tournament.MANUAL_TOURNAMENT_ID.equals(tournament.getTournamentID()))
			return false;
		if (Tournament.ROUND_ROBIN_TOURNAMENT_ID.equals(tournament.getTournamentID()))
			return false;
		return true;
	}
	
	public class PlayerInfoForEditTournament{
		private String name;
		private boolean usable;
		private User owner;

		public PlayerInfoForEditTournament(String name, boolean usable, User owner) {
			this.name = name;
			this.usable = usable;
			this.owner = owner;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setUsable(boolean usable) {
			this.usable = usable;
		}

		public boolean isUsable() {
			return usable;
		}

		public void setOwner(User owner) {
			this.owner = owner;
		}

		public User getOwner() {
			return owner;
		}
	}
	
	public List<User> getUsers () throws SQLException {
		// TODO: get only logged-in users
		return db.getUsers(0, 10);
	}
	
}
