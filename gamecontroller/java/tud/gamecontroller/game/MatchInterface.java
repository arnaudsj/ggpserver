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

package tud.gamecontroller.game;

import java.util.Collection;
import java.util.List;

import tud.gamecontroller.players.Player;

public interface MatchInterface<TermType, StateType extends StateInterface<TermType, ? extends StateType>> {

	String getMatchID();

	GameInterface<TermType,StateType> getGame();

	int getStartclock();

	int getPlayclock();

	Collection<? extends Player<TermType>> getPlayers();

	List<? extends Player<TermType>> getOrderedPlayers();

	Player<TermType> getPlayer(RoleInterface<TermType> role);

}