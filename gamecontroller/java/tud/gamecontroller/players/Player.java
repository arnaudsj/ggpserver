/*
    Copyright (C) 2008-2010 Stephan Schiffel <stephan.schiffel@gmx.de>
                  2010 Nicolas JEAN <njean42@gmail.com>

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

package tud.gamecontroller.players;

import tud.auxiliary.NamedObject;
import tud.gamecontroller.ConnectionEstablishedNotifier;
import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.StateInterface;

public interface Player<TermType, StateType extends StateInterface<TermType, ? extends StateType>> extends NamedObject{
	
	public void gameStart(RunnableMatchInterface<TermType, StateType> match, RoleInterface<TermType> role, ConnectionEstablishedNotifier notifier);
	
	/* MODIFIED: we don't use a jointMove any more, rather the seesTerms
	 * - in GDL-II, they are just sent in place of the moves;
	 * - in regular GDL, we want to put the jointMove that the players performed as if they were seesTerms
	 * 		derived from the rules:
	 * 			- sees(player1, move(player2, moveX)) <- does(player2, moveX)
	 * 			- and likewise for every other pair of players
	 */
	public MoveInterface<TermType> gamePlay(Object seesFluents, ConnectionEstablishedNotifier notifier);
	
	public void gameStop(Object seesTerms, ConnectionEstablishedNotifier notifier);
	/**
	 * 
	 * @return the total runtime of the player in milliseconds
	 */
	public long getTotalRuntime();
	
	public GDLVersion getGdlVersion();
	
}
