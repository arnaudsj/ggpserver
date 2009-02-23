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
 * Builds a leaderboard -- i.e., an output of the development of the players'
 * ratings over time. This can be a file or a graphical representation.
 * 
 * The order in which the following methods have to be used is the following:
 * 1. initialize()
 * 2. repeat 0-* times: 
 * 		a. beginMatchSet
 * 		b. repeat 0-* times:
 * 			i. ratingUpdate
 * 		c. endMatchSet
 * 3. finish()
 * 
 * @author martin
 *
 */
public interface OutputBuilder {

	/**
	 * Initializes the output of this builder, notifies it of the list of 
	 * all players.
	 * 
	 * @param players 
	 * 		the List of all players that will occur in the match set
	 * @throws IOException
	 * 		if something goes wrong
	 */
	public abstract void initialize(List<Player> players) throws IOException;
	
	/**
	 * Tells the Builder that a new MatchSet starts. This means that the old
	 * MatchSet must have been ended via endMatchSet().
	 * 
	 * @param matchSet
	 */
	public abstract void beginMatchSet(MatchSet matchSet);

	/**
	 * Tells the Builder that the currently running MatchSet is over.
	 * beginMatchSet() must have been called before. matchSet must be
	 * the same used in beginMatchSet.
	 * 
	 * @param matchSet
	 */
	public abstract void endMatchSet(MatchSet matchSet);

	/**
	 * Notifies the Builder of a new rating update. Must only be called 
	 * between begin and end of a MatchSet.
	 *   
	 * @param rating
	 */
	public abstract void ratingUpdate(Rating rating);

	/**
	 * Finishes output of this Builder. Calling any more methods after this
	 * has been called is illegal.
	 * @throws IOException 
	 */
	public abstract void finish();

}