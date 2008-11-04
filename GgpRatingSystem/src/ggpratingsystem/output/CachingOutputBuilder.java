package ggpratingsystem.output;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ggpratingsystem.MatchSet;
import ggpratingsystem.Player;
import ggpratingsystem.ratingsystems.Rating;
import ggpratingsystem.ratingsystems.RatingSystemType;

/**
 * This class caches all calls in memory and then, on finish(), passes them
 * all at once to an OutputBuilder. The advantage is that the list of players 
 * does not have to be known in advance.  
 * 
 * @author martin
 *
 */
public class CachingOutputBuilder implements OutputBuilder {
	private final RatingsWriter writer;
	private final RatingSystemType type;
	private final Set<Player> ignorePlayers;
	
	private Set<Player> players = new HashSet<Player>();
	
	private List<MatchSet> matchSets = new LinkedList<MatchSet>();
	private List<List<Rating>> ratingLists = new LinkedList<List<Rating>>();

	public CachingOutputBuilder(final RatingsWriter writer, final RatingSystemType type, 
			final Set<Player> ignorePlayers) {
		this.writer = writer;
		this.type = type;
		this.ignorePlayers = ignorePlayers;
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
		OutputBuilder decorated;
		
		
		List<Player> playerlist = new LinkedList<Player>(players);
		for (Player player : ignorePlayers) {
			playerlist.remove(player);
		}
		
		try {
			decorated = new DirectOutputBuilder(writer, playerlist, type);
			
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
