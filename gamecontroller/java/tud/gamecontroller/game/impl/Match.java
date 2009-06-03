/*
    Copyright (C) 2008 Stephan Schiffel <stephan.schiffel@gmx.de>

    This file is part of GameController.

    GameController is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GameController is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GameController.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.gamecontroller.game.impl;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.term.TermInterface;

public class Match<
		TermType extends TermInterface,
		ReasonerStateInfoType
		> implements MatchInterface<TermType, State<TermType, ReasonerStateInfoType>> {
	private String matchID;
	private GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game;
	private int startclock;
	private int playclock;
	private Map<? extends RoleInterface<TermType>, ? extends Player<TermType>> players;
	private List<Player<TermType>> orderedPlayers=null;
	
	public Match(String matchID, GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game, int startclock, int playclock, Map<? extends RoleInterface<TermType>, ? extends Player<TermType>> players){
		this.matchID=matchID;
		this.game=game;
		this.startclock=startclock;
		this.playclock=playclock;
		this.players=players;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.MatchInterface#getMatchID()
	 */
	public String getMatchID() {
		return matchID;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.MatchInterface#getGame()
	 */
	public GameInterface<TermType, State<TermType, ReasonerStateInfoType>> getGame() {
		return game;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.MatchInterface#getStartclock()
	 */
	public int getStartclock() {
		return startclock;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.MatchInterface#getPlayclock()
	 */
	public int getPlayclock() {
		return playclock;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.MatchInterface#getPlayers()
	 */
	public Collection<? extends Player<TermType>> getPlayers() {
		return players.values();
	}
	
	protected boolean hasPlayers() {
		return (players != null);
	}
	
	protected void setPlayers(Map<? extends RoleInterface<TermType>, ? extends Player<TermType>> players) {
		this.players = players;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.MatchInterface#getOrderedPlayers()
	 */
	public List<? extends Player<TermType>> getOrderedPlayers() {
		if(orderedPlayers==null){
			orderedPlayers=new LinkedList<Player<TermType>>();
			for(RoleInterface<TermType> role:game.getOrderedRoles()){
				orderedPlayers.add(players.get(role));
			}
		}
		return orderedPlayers;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.MatchInterface#getPlayer(RoleType)
	 */
	public Player<TermType> getPlayer(RoleInterface<TermType> role) {
		return players.get(role);
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((matchID == null) ? 0 : matchID.hashCode());
		return result;
	}

	/**
	 * Two matches are considered equal iff their matchID is equal (i.e., matchID is a unique identifier).
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Match other = (Match) obj;
		if (matchID == null) {
			if (other.matchID != null)
				return false;
		} else if (!matchID.equals(other.matchID))
			return false;
		return true;
	}
}
