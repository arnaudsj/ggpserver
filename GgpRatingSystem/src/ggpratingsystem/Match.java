/*
    Copyright (C) 2008,2009 Martin Günther <mintar@gmx.de>
                  2009 Stephan Schiffel <stephan.schiffel@gmx.de>
                       
    This file is part of GgpRatingSystem.

    GgpRatingSystem is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GgpRatingSystem is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GgpRatingSystem.  If not, see <http://www.gnu.org/licenses/>.
*/

package ggpratingsystem;

import java.util.List;
import java.util.logging.Logger;

/**
 * Match.java
 * 
 * This class represents a single played match.
 * It is responsible for storing the match data and for reading 
 * match data from various sources of information.
 */

public class Match {
	private static final Logger log = Logger.getLogger(Match.class.getName());
	
	static {
		// inherit default level for package ggpratingsystem
		log.setLevel(null);
	}
	
	private final MatchSet matchSet;
	
	private final String matchId;
	private final List<Player> players;
	private final List<Integer> scores;
	
	public Match(MatchSet matchSet, String matchId, List<Player> players, List<Integer> scores) {
		this.matchSet = matchSet;
		this.matchId = matchId;
		this.players = players;
		this.scores = scores;
	}

	public String getMatchId() {
		return matchId;
	}

	public MatchSet getMatchSet() {
		return matchSet;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public List<Integer> getScores() {
		return scores;
	}

	@Override
	public String toString() {
		return getMatchId();
	}
	
	/**
	 * simply print out some debug info
	 */
	public void print() {
		String result = toString() + ": " + players.get(0) + "(" + scores.get(0) + ") "; 
		
		for (int i = 1; i < players.size(); i++) {
			Player player = players.get(i);
			result += " vs. " + player + "(" + scores.get(i) + ") ";
		}
		System.out.println(result);
	}
}
