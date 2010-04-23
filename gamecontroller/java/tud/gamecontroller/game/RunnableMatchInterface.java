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

package tud.gamecontroller.game;

import java.util.Collection;
import java.util.List;

import tud.gamecontroller.logging.ErrorMessageListener;
import tud.gamecontroller.players.Player;

/**
 * This interface represents a runnable match. In contrast to MatchInterface,
 * this interface provides methods to get executable Players instead of just
 * PlayerInfos. This is only needed if the match is actually to be run.
 */
public interface RunnableMatchInterface<TermType, StateType extends StateInterface<TermType, ? extends StateType>>
		extends MatchInterface<TermType, StateType>, ErrorMessageListener {

	public Collection<? extends Player<TermType, StateType>> getPlayers();

	public List<? extends Player<TermType, StateType>> getOrderedPlayers();

	public Player<TermType, StateType> getPlayer(RoleInterface<TermType> role);
}