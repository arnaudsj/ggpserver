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

import ggpratingsystem.MatchSet;
import ggpratingsystem.Player;
import ggpratingsystem.ratingsystems.Rating;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * This class caches all calls in memory and then, on finish(), passes them
 * all at once to an OutputBuilder. The advantage is that the list of players 
 * does not have to be known in advance.  
 * 
 * @author martin
 *
 */
public class CachingOutputBuilder implements OutputBuilder {
	private final Set<Player> ignorePlayers;

	private final OutputBuilder decorated;
	

	private Set<Player> players = new HashSet<Player>();
	
	private List<MatchSet> matchSets = new LinkedList<MatchSet>();
	private List<List<Rating>> ratingLists = new LinkedList<List<Rating>>();

	public CachingOutputBuilder(final OutputBuilder decorated) {
		this(decorated, new HashSet<Player>());
	}

	public CachingOutputBuilder(final OutputBuilder decorated, 
			final Set<Player> ignorePlayers) {
		this.decorated = decorated;
		this.ignorePlayers = ignorePlayers;
	}




	/**
	 * The player list can be null, because it will be ignored. The whole point of  
	 * this class is to calculate the player list and pass it on to the decorated 
	 * OutputBuilder.
	 * 
	 * @see ggpratingsystem.output.OutputBuilder#initialize(java.util.List)
	 */
	public void initialize(List<Player> players) throws IOException {
		// nothing to do		
	}
	
	public void beginMatchSet(MatchSet matchSet) {
		matchSets.add(matchSet);
		ratingLists.add(new LinkedList<Rating>());

	}

	public void ratingUpdate(Rating rating) {
		List<Rating> ratings = ratingLists.get(ratingLists.size() - 1);
		try {
			ratings.add((Rating) rating.clone());
			players.add(rating.getPlayer());
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
	}
	
	public void endMatchSet(MatchSet matchSet) {
		// nothing to do
	}

	public void finish()  {
		List<Player> playerlist = new LinkedList<Player>(players);
		for (Player player : ignorePlayers) {
			playerlist.remove(player);
		}
		
		try {
			decorated.initialize(playerlist);
			
			int i = 0;
			assert (matchSets.size() == ratingLists.size());
			for (MatchSet matchSet : matchSets) {
				decorated.beginMatchSet(matchSet);

				List<Rating> ratings = ratingLists.get(i);
				
				for (Rating rating : ratings) {
					if (!ignorePlayers.contains(rating.getPlayer())) {
						decorated.ratingUpdate(rating);
					}
				}
				
				decorated.endMatchSet(matchSet);
				i++;
			}
			
			decorated.finish();
		} catch (IOException e) {
			// The worst thing that could happen is that the decorated output
			// builder did not finish correctly. Print message but don't rethrow
			// the exception.
			e.printStackTrace();
		}
	}
}
