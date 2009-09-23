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

package tud.gamecontroller.players;

import tud.auxiliary.NamedObject;
import tud.gamecontroller.ConnectionEstablishedNotifier;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;

public interface Player<TermType> extends NamedObject{
	public void gameStart(MatchInterface<TermType, ?> match, RoleInterface<TermType> role, ConnectionEstablishedNotifier notifier);
	public MoveInterface<TermType> gamePlay(JointMoveInterface<TermType> jointMove, ConnectionEstablishedNotifier notifier);
	public void gameStop(JointMoveInterface<TermType> jointMove, ConnectionEstablishedNotifier notifier);
	/**
	 * 
	 * @return the total runtime of the player in milliseconds
	 */
	public long getTotalRuntime();
}
