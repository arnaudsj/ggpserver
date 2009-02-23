/*
    Copyright (C) 2008,2009 Martin Günther <mintar@gmx.de>

    This file is part of GgpRatingSystem.

    GgpRatingSystem is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GgpRatingSystem is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GgpRatingSystem.  If not, see <http://www.gnu.org/licenses/>.
*/

package ggpratingsystem.ratingsystems;

import ggpratingsystem.Game;
import ggpratingsystem.MatchSet;

public abstract class AbstractGameInfo {
	private final Game game;

	/**
     * @deprecated Use {@link ggpratingsystem.ratingsystems.GameInfoFactory#makeGameInfo(RatingSystemType, Game)} instead.
	 */
	@Deprecated
	protected AbstractGameInfo(final Game game) {
		super();
		this.game = game;
	}

	public abstract RatingSystemType getType();

	public Game getGame() {
		return game;
	}
	
	public abstract void updateGameInfo(MatchSet matches);

	/**
	 * Resets the game info to the default values.
	 */
	public abstract void reset();
	
}
