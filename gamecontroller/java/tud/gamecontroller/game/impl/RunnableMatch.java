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

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.term.TermInterface;

public class RunnableMatch<TermType extends TermInterface, ReasonerStateInfoType>
		extends Match<TermType, ReasonerStateInfoType>
		implements RunnableMatchInterface<TermType, State<TermType, ReasonerStateInfoType>> {
	private final Map<? extends RoleInterface<TermType>, ? extends Player<TermType, State<TermType, ReasonerStateInfoType>>> players;
	private List<Player<TermType, State<TermType, ReasonerStateInfoType>>> orderedPlayers = null;
	
	public RunnableMatch(String matchID, GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game, int startclock, int playclock, Map<? extends RoleInterface<TermType>, ? extends Player<TermType, State<TermType, ReasonerStateInfoType>>> players){
		super(matchID, game, startclock, playclock);
		this.players=players;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.RunnableMatchInterface#getPlayers()
	 */
	public Collection<? extends Player<TermType, State<TermType, ReasonerStateInfoType>>> getPlayers() {
		return players.values();
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.RunnableMatchInterface#getOrderedPlayers()
	 */
	public List<? extends Player<TermType, State<TermType, ReasonerStateInfoType>>> getOrderedPlayers() {
		if(orderedPlayers==null){
			orderedPlayers=new LinkedList<Player<TermType, State<TermType, ReasonerStateInfoType>>>();
			for(RoleInterface<TermType> role:getGame().getOrderedRoles()){
				orderedPlayers.add(players.get(role));
			}
		}
		return orderedPlayers;
	}
	
	@Override
	public List<String> getOrderedPlayerNames() {
		ArrayList<String> orderedPlayerNames = new ArrayList<String>();
		List<? extends Player<?, ?>> orderedPlayerInfos = this.getOrderedPlayers();
		for (Player<?,?> info: orderedPlayerInfos)
			orderedPlayerNames.add(info.getName());
		return orderedPlayerNames;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.RunnableMatchInterface#getPlayer(RoleType)
	 */
	public Player<TermType, State<TermType, ReasonerStateInfoType>> getPlayer(RoleInterface<TermType> role) {
		return players.get(role);
	}

	public void notifyErrorMessage(GameControllerErrorMessage errorMessage) {
		// ignore error messages
	}
	
}
