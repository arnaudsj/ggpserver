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

package tud.gamecontroller.game.impl;

import java.util.List;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.term.TermInterface;

public abstract class Match<TermType extends TermInterface, ReasonerStateInfoType> implements MatchInterface<TermType, State<TermType, ReasonerStateInfoType>> {
	private final String matchID;
	private final GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game;
	private final int startclock;
	private final int playclock;
	


	public Match(final String matchID, final GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game, final int startclock, final int playclock) {
		super();
		this.matchID = matchID;
		this.game = game;
		this.startclock = startclock;
		this.playclock = playclock;
	}

	public String getMatchID() {
		return matchID;
	}

	public GameInterface<TermType, State<TermType, ReasonerStateInfoType>> getGame() {
		return game;
	}

	public int getStartclock() {
		return startclock;
	}

	public int getPlayclock() {
		return playclock;
	}
	
	public abstract List<String> getOrderedPlayerNames();
	
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
		final Match<?, ?> other = (Match<?, ?>) obj;
		if (matchID == null) {
			if (other.matchID != null)
				return false;
		} else if (!matchID.equals(other.matchID))
			return false;
		return true;
	}
}