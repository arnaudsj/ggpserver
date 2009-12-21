/*
    Copyright (C) 2009 Stephan Schiffel <stephan.schiffel@gmx.de> 

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

package tud.ggpserver.ratingsystem;

import ggpratingsystem.Configuration;
import ggpratingsystem.Game;
import ggpratingsystem.Match;
import ggpratingsystem.MatchSet;
import ggpratingsystem.MatchSetReader;
import ggpratingsystem.Player;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnector;
import tud.ggpserver.datamodel.Tournament;
import tud.ggpserver.datamodel.matches.ServerMatch;

/**
 * DBMatchSetReader reads MatchSets of the round_robin_tournament from the database.
 * <br>
 * A MatchSet is a set of matches of the same game consisting of at least one match more than players in the game, played over the course of at least matchSetMinTime and at most matchSetMaxTime. 
 */
public class DBMatchSetReader implements MatchSetReader {

	private static final AbstractDBConnector<?, ?> db = DBConnector.getInstance();

	private static final Logger logger = Logger.getLogger(DBMatchSetReader.class.getName());

	private Configuration configuration;
	
	private List<? extends ServerMatch<?,?>> matches;
	
	private int matchSetId = 0;

	private static final long matchSetMinTime = 24*60*60*1000L; // one day
	private static final long matchSetMaxTime = 4*7*24*60*60*1000L; // four weeks
	
	public DBMatchSetReader(Configuration configuration) throws SQLException {
		this.configuration = configuration;

		// filter matches, such that only finished matches are in the list
		matches = db.getMatches(0, Integer.MAX_VALUE, null, null, Tournament.ROUND_ROBIN_TOURNAMENT_ID, null, null, true);
		Iterator<? extends ServerMatch<?, ?>> it = matches.iterator();
		while(it.hasNext()){
			if ( it.next().getStatus() != ServerMatch.STATUS_FINISHED ) {
				it.remove();
			}
		}
	}
	
	@Override
	public boolean hasNext() {
		return !matches.isEmpty();
	}
	
	@Override
	public MatchSet readMatchSet() {
		if(matches.isEmpty())
			return null;
		
		Iterator<? extends ServerMatch<?, ?>> it = matches.iterator();
		if ( !it.hasNext() ) {
			return null;
		}
		ServerMatch<?, ?> nextMatch = it.next();
		
		matchSetId++;
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(nextMatch.getStartTime());
		long startTimeInMillis = nextMatch.getStartTime().getTime();
		
		String gameName = nextMatch.getGame().getName();
		Game game = configuration.getGameSet().getGame(gameName);
		
		if(!game.hasRoles()) {
			List<String> roleNames = new LinkedList<String>();
			for(RoleInterface<?> role:nextMatch.getGame().getOrderedRoles()){
				roleNames.add(role.getPrefixForm());
			}
			game.setRoles(roleNames);
		}
		
		logger.info("creating MatchSet " + matchSetId + " of game " + gameName + " (start time: " + startTimeInMillis + ")");
		MatchSet matchSet = new MatchSet(""+matchSetId, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), matchSetId, game);
		
		boolean continueSearch = true;
		int nbOfMatches = 0;
		final int nbOfRoles = game.getRoles().size();
		do {
			if ( nextMatch.getGame().getName().equals(gameName) ) {
				logger.info("add match " + nextMatch.getMatchID() + " to matchset");
				matchSet.addMatch(getRatingSystemMatch(matchSet, nextMatch));
				it.remove();
				nbOfMatches++;
			}
			if ( it.hasNext() ) {
				nextMatch = it.next();
				long nextMatchStartTime = nextMatch.getStartTime().getTime();
				if (nbOfMatches > nbOfRoles && nextMatchStartTime > startTimeInMillis + matchSetMinTime) {
					continueSearch = false;
				} else if (nextMatchStartTime > startTimeInMillis + matchSetMaxTime ) {
					continueSearch = false;
				}
			} else {
				continueSearch = false;
			}
		} while ( continueSearch );
		return matchSet;
	}
	
	private Match getRatingSystemMatch(MatchSet matchSet, ServerMatch<?, ?> match){
		List<Player> players = new LinkedList<Player>();
		for(PlayerInfo playerInfo:match.getOrderedPlayerInfos()) {
			players.add(configuration.getPlayerSet().getPlayer(playerInfo.getName()));
		}
		List<Integer> scores = match.getOrderedGoalValues();
		return new Match(matchSet, match.getMatchID(), players, scores); 
	}

}