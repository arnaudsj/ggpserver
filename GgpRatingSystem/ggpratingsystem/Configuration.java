package ggpratingsystem;

import static ggpratingsystem.ratingsystems.RatingSystemType.DYNAMIC_LINEAR_REGRESSION;

import ggpratingsystem.output.OutputBuilder;
import ggpratingsystem.ratingsystems.RatingStrategy;
import ggpratingsystem.ratingsystems.RatingSystemType;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * This class holds the configuration of currently enabled
 *    - Strategies, 
 *    - GameInfos, and
 *    - Ratings.
 * 
 * Also, it acts as a factory for these classes.
 * 
 * Finally, it is responsible for
 *    - updating GameInfos and
 *    - updating Ratings.
 * 
 * 
 * @author martin
 *
 */
public class Configuration {
	private Map<RatingSystemType, List<OutputBuilder>> outputBuilders = new HashMap<RatingSystemType, List<OutputBuilder>>();	
	private Map<RatingSystemType, RatingStrategy> ratingSystems = new HashMap<RatingSystemType, RatingStrategy>();

	public void addRatingSystem(RatingStrategy ratingSystem) {
		ratingSystems.put(ratingSystem.getType(), ratingSystem);
	}
	
	public void addOutputBuilder(RatingSystemType type, OutputBuilder builder) {
		if (!isEnabled(type)) {
			throw new IllegalArgumentException("RatingSystemType " + type + " must be enabled before adding any OutputBuilders to it!");
		}
			
		List<OutputBuilder> builders = outputBuilders.get(type);
		if (builders == null) {
			builders = new LinkedList<OutputBuilder>();
			outputBuilders.put(type, builders);
		}
		
		builders.add(builder);
	}

	public boolean isEnabled(RatingSystemType type) {
		return ratingSystems.keySet().contains(type);
	}

	public Set<RatingSystemType> getEnabledRatingSystems() {
		return ratingSystems.keySet();
	}

	/**
	 * @param matchSet
	 */
	public void processMatchSet(MatchSet matchSet) {
		for (RatingStrategy ratingSystem : ratingSystems.values()) {
			ratingSystem.update(matchSet);
			RatingSystemType type = ratingSystem.getType(); 

			List<OutputBuilder> builders = outputBuilders.get(type);

			for (OutputBuilder builder : builders) {
				builder.beginMatchSet(matchSet);
			}
			
			List<Match> matchList = matchSet.getMatches();
			for (Match match : matchList) {
				List<Player> playersInMatch = match.getPlayers();
				
				for (Player player : playersInMatch) {
					for (OutputBuilder builder : builders) {
						builder.ratingUpdate(player.getRating(DYNAMIC_LINEAR_REGRESSION));
					}
				}
			}
			
			for (OutputBuilder builder : builders) {
				builder.endMatchSet(matchSet);
			}
		}
	}

	/**
	 * Has to be called after processing the last MatchSet
	 * @throws IOException
	 */
	public void closeOutputBuilders() throws IOException {
		for (List<OutputBuilder> builders : outputBuilders.values()) {
			for (OutputBuilder builder : builders) {
				builder.finish();
			}
		}
	}
}
