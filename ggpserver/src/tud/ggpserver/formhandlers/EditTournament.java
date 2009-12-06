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

package tud.ggpserver.formhandlers;

import static tud.ggpserver.datamodel.DBConnectorFactory.getDBConnector;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.logging.Logger;

import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.players.LegalPlayerInfo;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.ggpserver.datamodel.Game;
import tud.ggpserver.datamodel.RemotePlayerInfo;
import tud.ggpserver.datamodel.Tournament;
import tud.ggpserver.datamodel.User;
import tud.ggpserver.datamodel.matches.NewMatch;
import tud.ggpserver.datamodel.matches.RunningMatch;
import tud.ggpserver.datamodel.matches.ScheduledMatch;
import tud.ggpserver.datamodel.matches.ServerMatch;
import tud.ggpserver.scheduler.MatchRunner;

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
	private List<PlayerInfoForEditTournament> playerInfos = null;
	
	private String errorString = "No error";

	@SuppressWarnings("unchecked")
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
		if (tournament == null || user == null)
			return false;
		
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
	
	public boolean isValid() throws SQLException {
		if (!isAllow())
			return false;
		
		if (action.equals(ADD_MATCH)) {
			return true;
		} else if (action.equals(START_MATCH) && match != null && match instanceof NewMatch && match.getOwner().equals(user)) {
			// check that all players are active
			// TODO: somehow tell the user why the action was not performed 
			
			// There might still be a race condition such that a match is scheduled but never run (e.g., if a player goes offline
			// right after this check), but we avoid most cases here. 
			for(PlayerInfo playerInfo:match.getPlayerInfos()) {
				if(playerInfo instanceof RemotePlayerInfo) {
					RemotePlayerInfo remotePlayerInfo = (RemotePlayerInfo)playerInfo;
					if(!remotePlayerInfo.getStatus().equals(RemotePlayerInfo.STATUS_ACTIVE)) {
						errorString = "start match is invalid: "+remotePlayerInfo.getName()+" is " + remotePlayerInfo.getStatus() + "(not " + RemotePlayerInfo.STATUS_ACTIVE + ")";
						logger.warning(errorString);
						return false;
					}
					if(!remotePlayerInfo.isAvailableForManualMatches() && !remotePlayerInfo.getOwner().equals(user)) {
						errorString = "start match is invalid: "+remotePlayerInfo.getName()+" is not available and not owned by "+user.getUserName();
						logger.warning(errorString);
						return false;
					}
				}
			}
			return true;
		} else if (action.equals(ABORT_MATCH) && match != null && (match instanceof ScheduledMatch || match instanceof RunningMatch) && match.getOwner().equals(user)) {
			return true;
		} else if (action.equals(DELETE_MATCH) && match != null && match.getOwner().equals(user)) {
			return true;
		} else if (action.equals(CLONE_MATCH) && match != null) {
			return true;
		}
		errorString = "action '"+action+"' is invalid for match '"+match.getMatchID() + "'";
		logger.warning(errorString);
		return false;
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
			rolesToPlayerInfos.put(role, new RandomPlayerInfo(roleIndex));
			roleIndex++;
		}
		
		db.createMatch(game, Tournament.DEFAULT_STARTCLOCK,
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
	
	@SuppressWarnings("unchecked")
	public List<PlayerInfo> getEnabledPlayerInfos() throws SQLException {
		List<PlayerInfo> result = new LinkedList<PlayerInfo>();
		result.add(new RandomPlayerInfo(-1));
		result.add(new LegalPlayerInfo(-1));
		result.addAll(db.getPlayerInfos(RemotePlayerInfo.STATUS_ACTIVE));

		return result;
	}

	@SuppressWarnings("unchecked")
	public List<PlayerInfoForEditTournament> getPlayerInfos() throws SQLException {
		if(playerInfos == null) {
			playerInfos = new LinkedList<PlayerInfoForEditTournament>();
			User admin=db.getAdminUser();
			// add local players
			playerInfos.add(new PlayerInfoForEditTournament(new RandomPlayerInfo(-1).getName(), true, admin));
			playerInfos.add(new PlayerInfoForEditTournament(new LegalPlayerInfo(-1).getName(), true, admin));
			List<? extends RemotePlayerInfo> allRemotePlayers = db.getPlayerInfos();
			// add all available players
			ListIterator<? extends RemotePlayerInfo> i = allRemotePlayers.listIterator();
			while(i.hasNext()){
				RemotePlayerInfo p = i.next();
				if(p.getStatus().equals(RemotePlayerInfo.STATUS_ACTIVE) &&
					( p.isAvailableForManualMatches() || p.getOwner().equals(user))
					) {
						playerInfos.add(new PlayerInfoForEditTournament(p.getName(), true, p.getOwner()));
						i.remove();
				}
			}
			// add all remaining players (but mark them as unavailable)
			i = allRemotePlayers.listIterator();
			while(i.hasNext()){
				RemotePlayerInfo p = i.next();
				playerInfos.add(new PlayerInfoForEditTournament(p.getName(), false, p.getOwner()));
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
}
