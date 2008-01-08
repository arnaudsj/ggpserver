package ggpratingsystem.output;

import java.io.IOException;
import java.io.Writer;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import ggpratingsystem.AbstractRating;
import ggpratingsystem.MatchSet;
import ggpratingsystem.Player;
import ggpratingsystem.RatingSystemType;

/**
 * This class caches all calls in memory and then, on finish(), passes them
 * all at once to a CSVOutputBuilder. The advantage is that the list of players 
 * does not have to be known in advance.  
 * 
 * @author martin
 *
 */
public class CachingCSVOutputBuilder implements OutputBuilder {
	private final Writer writer;
	private final RatingSystemType type;
	private final double[] initialRatings;
	
	private Set<Player> players = new HashSet<Player>();
	
	private List<MatchSet> matchSets = new LinkedList<MatchSet>();
	private List<List<AbstractRating>> ratingLists = new LinkedList<List<AbstractRating>>();  

	public CachingCSVOutputBuilder(final Writer writer, final RatingSystemType type) {
		super();
		this.writer = writer;
		this.type = type;
		this.initialRatings = null;
	}

	public CachingCSVOutputBuilder(final Writer writer, final double[] initialRatings) {
		super();
		this.writer = writer;
		this.type = null;
		this.initialRatings = initialRatings;
	}

	public void beginMatchSet(MatchSet matchSet) {
		matchSets.add(matchSet);
		ratingLists.add(new LinkedList<AbstractRating>());

	}

	public void ratingUpdate(AbstractRating rating) {
		List<AbstractRating> ratings = ratingLists.get(ratingLists.size() - 1);
		ratings.add(rating);	// TODO: clone this here or make rating immutable
		players.add(rating.getPlayer());
	}
	
	public void endMatchSet(MatchSet matchSet) {
		// nothing to do
	}

	public void finish() throws IOException {
		CSVOutputBuilder decorated;
		
		List<Player> playerlist = new LinkedList<Player>(players);
		
		assert ((type == null && initialRatings != null) || (type != null && initialRatings == null));		
		if (type != null) {
			decorated = new CSVOutputBuilder(writer, playerlist, type);
		} else {
			decorated = new CSVOutputBuilder(writer, playerlist, initialRatings);
		}
		
		int i = 0;
		assert (matchSets.size() == ratingLists.size());
		for (MatchSet matchSet : matchSets) {
			decorated.beginMatchSet(matchSet);

			List<AbstractRating> ratings = ratingLists.get(i);
			
			for (AbstractRating rating : ratings) {
				decorated.ratingUpdate(rating);
			}
			
			decorated.endMatchSet(matchSet);
			i++;
		}
		
		decorated.finish();
	}
}
