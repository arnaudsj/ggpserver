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

package ggpratingsystem.output;

import java.io.IOException;
import java.util.List;

import ggpratingsystem.MatchSet;
import ggpratingsystem.Player;
import ggpratingsystem.ratingsystems.Rating;

/**
 * Implementation of OutputBuilder which validates the constraints on the method call order.
 * Can be used as a decorator for other OutputBuilders.
 * 
 * @author martin
 *
 */
public class ValidatingOutputBuilder implements OutputBuilder {
	private final OutputBuilder decorated;
	private boolean initialized = false;
	private boolean finished = false;
	private MatchSet currentMatchSet;
	
	public ValidatingOutputBuilder(OutputBuilder decorated) {
		super();
		if (decorated == null) {
			throw new IllegalArgumentException("decorated may not be null!");
		}
		this.decorated = decorated;
	}

	public void initialize(List<Player> players) throws IOException {
		if (initialized) {
			throw new IllegalStateException("initialize() is called a second time!");
		}
		initialized = true;
		
		decorated.initialize(players);
	}
	
	/* (non-Javadoc)
	 * @see ggpratingsystem.output.OutputBuilder#beginMatchSet(ggpratingsystem.MatchSet)
	 */
	public void beginMatchSet(MatchSet matchSet) {
		if (finished) {
			throw new IllegalStateException("finish() has been called before!");
		}
		if (!initialized) {
			throw new IllegalStateException("initialize() has not been called yet!");
		}
		if (currentMatchSet != null) {
			throw new IllegalStateException("Old MatchSet has not been ended with endMatchSet() before calling beginMatchSet()!");
		}
		currentMatchSet = matchSet;
		
		decorated.beginMatchSet(matchSet);
	}
	
	/* (non-Javadoc)
	 * @see ggpratingsystem.output.OutputBuilder#endMatchSet(ggpratingsystem.MatchSet)
	 */
	public void endMatchSet(MatchSet matchSet) {
		if (finished) {
			throw new IllegalStateException("finish() has been called before!");
		}
		if (currentMatchSet == null) {
			throw new IllegalStateException("endMatchSet() was called before calling beginMatchSet()!");
		}
		if (!currentMatchSet.equals(matchSet)) {
			throw new IllegalStateException("endMatchSet() was called with a different argument than beginMatchSet()!");
		}
		currentMatchSet = null;
		
		decorated.endMatchSet(matchSet);
	}
	
	/* (non-Javadoc)
	 * @see ggpratingsystem.output.OutputBuilder#ratingUpdate(ggpratingsystem.AbstractRating)
	 */
	public void ratingUpdate(Rating rating) {
		if (finished) {
			throw new IllegalStateException("finish() has been called before!");
		}
		if (currentMatchSet == null) {
			throw new IllegalStateException("ratingUpdate() was called before calling beginMatchSet()!");
		}
		
		decorated.ratingUpdate(rating);
	}
	
	/* (non-Javadoc)
	 * @see ggpratingsystem.output.OutputBuilder#finish()
	 */
	public void finish() {
		if (finished) {
			throw new IllegalStateException("finish() is called a second time!");
		}
		finished = true;

		if (currentMatchSet != null) {
			throw new IllegalStateException("finish() was called before calling endMatchSet()!");
		}
		
		decorated.finish();
	}
}
