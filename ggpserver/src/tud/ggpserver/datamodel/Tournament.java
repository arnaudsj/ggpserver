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

import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.term.TermInterface;
import tud.ggpserver.datamodel.matches.ServerMatch;

public class Tournament<TermType extends TermInterface, ReasonerStateInfoType> {
	public static final int DEFAULT_STARTCLOCK = 600;
	public static final int DEFAULT_PLAYCLOCK = 30;
	
	private final String tournamentID;
	private final User owner;
	private Map<PlayerInfo, Integer> numberOfMatches = new HashMap<PlayerInfo, Integer>();
	private Map<PlayerInfo, Integer> totalReward = new HashMap<PlayerInfo, Integer>();
//	private List<Integer> orderedNumberOfMatches;
//	private List<Integer> orderedTotalRewards;
	private List<PlayerInfo> orderedPlayers;

	private final AbstractDBConnector<TermType, ReasonerStateInfoType> db;

	public Tournament(final String tournamentID, final User owner, AbstractDBConnector<TermType, ReasonerStateInfoType> db) {
		this.tournamentID = tournamentID;
		this.owner = owner;
		this.db = db;
	}

	public List<ServerMatch<TermType,ReasonerStateInfoType>> getMatches() throws SQLException {
		return db.getMatches(0, Integer.MAX_VALUE, null, null, tournamentID, false);
	}

	public String getTournamentID() {
		return tournamentID;
	}

	public User getOwner() {
		return owner;
	}

	public Map<PlayerInfo, Integer> getNumberOfMatches() {
		return new HashMap<PlayerInfo, Integer>(numberOfMatches);
	}

	public void setNumberOfMatches(Map<PlayerInfo, Integer> numberOfMatches) {
		this.numberOfMatches = new HashMap<PlayerInfo, Integer>(numberOfMatches);
	}

	public int getNumberOfMatches(PlayerInfo player) {
		return numberOfMatches.get(player);
	}

	public void setNumberOfMatches(PlayerInfo player, int newNumberOfMatches) {
		this.numberOfMatches.put(player, newNumberOfMatches);
	}
	
	public Map<PlayerInfo, Integer> getTotalReward() {
		return new HashMap<PlayerInfo, Integer>(totalReward);
	}

	public void setTotalReward(Map<PlayerInfo, Integer> totalReward) {
		this.totalReward = new HashMap<PlayerInfo, Integer>(totalReward);
	}

	public int getTotalReward(PlayerInfo player) {
		return totalReward.get(player);
	}

	public void setTotalReward(PlayerInfo player, int newTotalReward) {
		this.totalReward.put(player, newTotalReward);
	}
	
	public Map<PlayerInfo, Double> getAverageReward() {
		Map<PlayerInfo, Double> averageReward = new HashMap<PlayerInfo, Double>();
		for (PlayerInfo playerInfo : getOrderedPlayers()) {
			int count = getNumberOfMatches(playerInfo);
			if (count > 0) {
				averageReward.put(playerInfo, ((double) getTotalReward(playerInfo)) / count);
			} else {
				averageReward.put(playerInfo, 0.0);
			}
		}
		return averageReward;
	}

	public List<PlayerInfo> getOrderedPlayers() {
		if (orderedPlayers == null) {
			orderedPlayers = initOrderedPlayers();
		}
		return orderedPlayers;
	}
	
	private List<PlayerInfo> initOrderedPlayers() {
		List<PlayerInfo> result = new LinkedList<PlayerInfo>();
		
		List<Entry<PlayerInfo, Integer>> entryList = new LinkedList<Entry<PlayerInfo, Integer>>(totalReward.entrySet());
		Collections.sort(entryList
				, new Comparator<Entry<PlayerInfo, Integer>>() {
			@Override
			public int compare(Entry<PlayerInfo, Integer> firstEntry,
					Entry<PlayerInfo, Integer> secondEntry) {
				return secondEntry.getValue() - firstEntry.getValue();
			}
		});
		
		for (Entry<PlayerInfo, Integer> entry : entryList) {
			result.add(entry.getKey());
		}
		return result;
	}
	
	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((tournamentID == null) ? 0 : tournamentID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Tournament other = (Tournament) obj;
		if (tournamentID == null) {
			if (other.tournamentID != null)
				return false;
		} else if (!tournamentID.equals(other.tournamentID))
			return false;
		return true;
	}
	
}
