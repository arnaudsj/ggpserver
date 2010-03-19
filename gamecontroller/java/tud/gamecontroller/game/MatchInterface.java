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

import java.util.List;

/**
 * The point of this interfaces is not to refer to the actual Player
 * implementation, only to PlayerInfos. This makes Matches that are not intended
 * to be run (e.g., finished or aborted matches) much more lightweight and
 * removes actual implementation details from these matches, because they do not
 * even need to know about reasoners.<br>
 * 
 * If you need a match that provides Player implementations instead of only
 * PlayerInfos, use RunnableMatchInterface.
 */
public interface MatchInterface<TermType, StateType extends StateInterface<TermType, ? extends StateType>> {

	public abstract String getMatchID();
	
	public abstract GameInterface<TermType, StateType> getGame();

	public abstract int getStartclock();

	public abstract int getPlayclock();
	
	public abstract List<String> getOrderedPlayerNames();
	
	// TODO: This could be extended to include the following methods:
	//	public abstract Collection<? extends PlayerInfo<TermType>> getPlayerInfos();
	//	public abstract List<? extends PlayerInfo> getOrderedPlayerInfos();
	//	public abstract PlayerInfo getPlayerInfo(RoleInterface<TermType> role);
}