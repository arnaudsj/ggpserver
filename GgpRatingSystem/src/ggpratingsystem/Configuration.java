package ggpratingsystem;

import ggpratingsystem.output.CachingCSVOutputBuilder;
import ggpratingsystem.output.OutputBuilder;
import ggpratingsystem.output.ValidatingOutputBuilder;
import ggpratingsystem.ratingsystems.RatingStrategy;
import ggpratingsystem.ratingsystems.RatingSystemType;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

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
 * Configuration acts as a facade to the whole GGP Subsystem, 
 * providing convenience methods for adding output builders etc. 
 * 
 * @author martin
 *
 */
public class Configuration {
	private static File outputDir = null;
	
	private Map<RatingSystemType, List<OutputBuilder>> outputBuilders = new HashMap<RatingSystemType, List<OutputBuilder>>();	
	private Map<RatingSystemType, RatingStrategy> ratingSystems = new HashMap<RatingSystemType, RatingStrategy>();
	private MatchReader matchReader;

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

	public void run() {
		if (matchReader == null) {
			throw new IllegalStateException("setMatchReader() must be called before calling run()!");
		}
		
		while (matchReader.hasNext()) {
			MatchSet matchSet = matchReader.readMatchSet();
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
							builder.ratingUpdate(player.getRating(type));
						}
					}
				}
				
				for (OutputBuilder builder : builders) {
					builder.endMatchSet(matchSet);
				}
			}
		}
		closeOutputBuilders();
	}

	public void closeOutputBuilders() {
		for (List<OutputBuilder> builders : outputBuilders.values()) {
			for (OutputBuilder builder : builders) {
				builder.finish();
			}
		}
	}

	public RatingStrategy getRatingSystem(RatingSystemType type) {
		return ratingSystems.get(type);
	}
	
	
	/** Convenience method for adding a CSV Output Builder to a rating system. */
	public void addCSVOutputBuilder(RatingSystemType type) throws IOException {
		Writer writer = new FileWriter(new File(getOutputDir(),
				getRatingSystem(type).idString() + ".csv"));
			// one separate file for each RatingSystem   

		OutputBuilder outputBuilder = 
			new ValidatingOutputBuilder(
				new CachingCSVOutputBuilder(writer, type));
		
		addOutputBuilder(type, outputBuilder);
	}

	public void setMatchReader(MatchReader matchReader) {
		this.matchReader = matchReader;
	}

	public static void setOutputDir(File outputDir) throws IOException {
		Configuration.outputDir = outputDir;
		
		/* create output dir if it does not exist */
		if (!outputDir.exists()) {
			boolean success = outputDir.mkdirs();
			if (!success) {
				throw new IOException("Directory " + outputDir + " could not be created!");
			}
		}
	}
	
	public static File getOutputDir() {
		if (outputDir == null) {
			throw new IllegalStateException("outputDir must be set before accessing it!");
		}
		
		return outputDir;
	}

	public void setDebugLevel(Level level) {
		Logger.getLogger("ggpratingsystem").setLevel(level);
	}
}
