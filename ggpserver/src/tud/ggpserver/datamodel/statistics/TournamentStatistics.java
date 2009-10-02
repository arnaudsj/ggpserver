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

package tud.ggpserver.datamodel.statistics;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.term.TermInterface;

public class TournamentStatistics<TermType extends TermInterface, ReasonerStateInfoType> {
	private final Map<PlayerInfo, Integer> numberOfMatches;
	private final Map<PlayerInfo, Integer> totalReward;
	private Map<PlayerInfo, Double> averageReward = null;
	private List<PlayerInfo> orderedPlayers = null;
	
	private final String tournamentID;
	
	private int sortBy;
	
	public static final int FIELD_PLAYER = 0;
	public static final int FIELD_NUMBER_OF_MATCHES = 1;
	public static final int FIELD_TOTAL_REWARD = 2;
	public static final int FIELD_AVERAGE_REWARD = 3;
	
	private int sortOrder;
	
	public static final int SORT_ORDER_ASCENDING = 0;
	public static final int SORT_ORDER_DESCENDING = 1;

	public TournamentStatistics(String tournamentID, Map<PlayerInfo, Integer> numberOfMatches, Map<PlayerInfo, Integer> totalReward) {
		this.tournamentID = tournamentID;
		this.numberOfMatches = numberOfMatches;
		this.totalReward = totalReward;
	}
	
	public String getTournamentID() {
		return tournamentID;
	}
	
	public Map<PlayerInfo, Integer> getNumberOfMatches() {
		return new HashMap<PlayerInfo, Integer>(numberOfMatches);
	}

	public int getNumberOfMatches(PlayerInfo player) {
		return numberOfMatches.get(player);
	}

	public void setNumberOfMatches(PlayerInfo player, int newNumberOfMatches) {
		numberOfMatches.put(player, newNumberOfMatches);
	}
	
	public Map<PlayerInfo, Integer> getTotalReward() throws SQLException {
		return new HashMap<PlayerInfo, Integer>(totalReward);
	}

	public int getTotalReward(PlayerInfo player) {
		return totalReward.get(player);
	}

	public void setTotalReward(PlayerInfo player, int newTotalReward) {
		totalReward.put(player, newTotalReward);
	}
	
	public Map<PlayerInfo, Double> getAverageReward() {
		if(averageReward == null) {
			averageReward = new HashMap<PlayerInfo, Double>();
			for (PlayerInfo playerInfo : getPlayers()) {
				int count = getNumberOfMatches(playerInfo);
				if (count > 0) {
					averageReward.put(playerInfo, ((double) getTotalReward(playerInfo)) / count);
				} else {
					averageReward.put(playerInfo, 0.0);
				}
			}
		}
		return averageReward;
	}

	public double getAverageReward(PlayerInfo player) {
		return getAverageReward().get(player).doubleValue();
	}

	public Collection<PlayerInfo> getPlayers() {
		return totalReward.keySet();
	}

	public List<PlayerInfo> getOrderedPlayers() {
		return getOrderedPlayers(FIELD_TOTAL_REWARD, SORT_ORDER_DESCENDING);
	}

	public List<PlayerInfo> getOrderedPlayers(int sortBy, int sortOrder) {
		if (orderedPlayers == null || sortBy != this.sortBy || sortOrder != this.sortOrder) {
			orderedPlayers = initOrderedPlayers(sortBy, sortOrder);
		}
		return orderedPlayers;
	}
	
	private List<PlayerInfo> initOrderedPlayers(final int sortBy, final int sortOrder) {
		List<PlayerInfo> result = new LinkedList<PlayerInfo>(getPlayers());
		Collections.sort(result
			, new Comparator<PlayerInfo>() {
				@Override
				public int compare(PlayerInfo firstEntry,
						PlayerInfo secondEntry) {
					int compare;
					switch(sortBy){
						case FIELD_PLAYER:
							compare = firstEntry.getName().compareToIgnoreCase(secondEntry.getName());
							break;
						case FIELD_NUMBER_OF_MATCHES:
							compare = getNumberOfMatches(firstEntry) - getNumberOfMatches(secondEntry);
							break;
						case FIELD_AVERAGE_REWARD:
							compare = Double.compare(getAverageReward(firstEntry), getAverageReward(secondEntry));
							break;
						default: // case SORT_FIELD_TOTAL_REWARD:
							compare = getTotalReward(firstEntry) - getTotalReward(secondEntry);
							break;
					}
					return sortOrder == SORT_ORDER_ASCENDING ? compare : -compare;
				}
			});
		return result;
	}
	

}
