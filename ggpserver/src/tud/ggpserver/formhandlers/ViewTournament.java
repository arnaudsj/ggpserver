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

package tud.ggpserver.formhandlers;

import java.sql.SQLException;
import java.util.List;

import tud.gamecontroller.players.PlayerInfo;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import tud.ggpserver.datamodel.Tournament;
import tud.ggpserver.datamodel.statistics.TournamentStatistics;

public class ViewTournament {
	
	private String tournamentID;
	
	private Tournament<? ,?> tournament = null;
	
	private TournamentStatistics<? ,?> tournamentStatistics = null;

	private String sortBy;
	
	private int sortByInt = TournamentStatistics.FIELD_TOTAL_REWARD;

	private String sortOrder;

	private int sortOrderInt = TournamentStatistics.SORT_ORDER_DESCENDING;
	
	public final static String[] fieldNames = {"player", "numberOfMatches", "totalReward", "averageReward"};

	public final static String[] fieldDescriptions = {"player", "number of matches", "total reward", "average reward"};
	
	public String[] getFieldNames() {
		return fieldNames;
	}

	public String[] getFieldDescriptions() {
		return fieldDescriptions;
	}

	public String getTournamentID() {
		return tournamentID;
	}

	public void setTournamentID(String tournamentID) throws SQLException {
		this.tournamentID = tournamentID;
		AbstractDBConnector<?, ?> db = DBConnectorFactory.getDBConnector();
		tournament = db.getTournament(tournamentID);
	}

	public Object getValue(PlayerInfo player, int field) throws SQLException {
		switch(field){
			case TournamentStatistics.FIELD_PLAYER: return player.getName();
			case TournamentStatistics.FIELD_NUMBER_OF_MATCHES: return getTournamentStatistics().getNumberOfMatches(player);
			case TournamentStatistics.FIELD_TOTAL_REWARD: return getTournamentStatistics().getTotalReward(player);
			case TournamentStatistics.FIELD_AVERAGE_REWARD: return getTournamentStatistics().getAverageReward(player);
			default: return null;
		}
	}
	
	/**
	 * 
	 * @param sortBy one of "player", "numberOfMatches", "totalReward", or "averageReward"
	 */
	public void setSortBy(String sortBy) {
		if(sortBy.equals(fieldNames[TournamentStatistics.FIELD_PLAYER]))
			this.sortByInt = TournamentStatistics.FIELD_PLAYER;
		else if(sortBy.equals(fieldNames[TournamentStatistics.FIELD_NUMBER_OF_MATCHES]))
			this.sortByInt = TournamentStatistics.FIELD_NUMBER_OF_MATCHES;
		else if(sortBy.equals(fieldNames[TournamentStatistics.FIELD_TOTAL_REWARD]))
			this.sortByInt = TournamentStatistics.FIELD_TOTAL_REWARD;
		else if(sortBy.equals(fieldNames[TournamentStatistics.FIELD_AVERAGE_REWARD]))
			this.sortByInt = TournamentStatistics.FIELD_AVERAGE_REWARD;
		else
			throw new IllegalArgumentException("unknown sort field:"+sortBy);
		this.sortBy = sortBy;
	}

	public String getSortBy() {
		return sortBy;
	}
	
	/**
	 * 
	 * @param sortOrder one of "asc", "desc"
	 */
	public void setSortOrder(String sortOrder) {
		if(sortOrder.equals("asc"))
			this.sortOrderInt = TournamentStatistics.SORT_ORDER_ASCENDING;
		else if(sortOrder.equals("desc"))
			this.sortOrderInt = TournamentStatistics.SORT_ORDER_DESCENDING;
		else
			throw new IllegalArgumentException("unknown sort order:"+sortOrder);
		this.sortOrder = sortOrder;
	}

	public String getSortOrder() {
		return sortOrder;
	}

	public List<PlayerInfo> getOrderedPlayers() throws SQLException {
		return getTournamentStatistics().getOrderedPlayers(sortByInt, sortOrderInt);
	}

	public Tournament<?, ?> getTournament() throws SQLException {
		if (tournament == null) {
			AbstractDBConnector<?, ?> db = DBConnectorFactory.getDBConnector();
			tournament = db.getTournament(tournamentID);
		}
		return tournament;
	}

	public TournamentStatistics<?, ?> getTournamentStatistics() throws SQLException {
		if (tournamentStatistics == null) {
			AbstractDBConnector<?, ?> db = DBConnectorFactory.getDBConnector();
			tournamentStatistics = db.getTournamentStatistics(tournamentID);
		}
		return tournamentStatistics;
	}
}
