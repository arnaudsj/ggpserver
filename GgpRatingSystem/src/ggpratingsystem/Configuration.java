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

package ggpratingsystem;

import ggpratingsystem.output.CSVRatingsWriter;
import ggpratingsystem.output.CachingOutputBuilder;
import ggpratingsystem.output.DirectOutputBuilder;
import ggpratingsystem.output.GnuPlotRatingsWriter;
import ggpratingsystem.output.HtmlOutputBuilder;
import ggpratingsystem.output.OutputBuilder;
import ggpratingsystem.output.ValidatingOutputBuilder;
import ggpratingsystem.ratingsystems.RatingException;
import ggpratingsystem.ratingsystems.RatingFactory;
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

	private File previousRatings;

	public void addRatingSystem(RatingStrategy ratingSystem) {
		ratingSystems.put(ratingSystem.getType(), ratingSystem);
	}
	
	/** Convenience method for adding a CSV Output Builder to a rating system. 
	 * @param ignorePlayers */
	public void addCSVOutputBuilder(RatingSystemType type, Set<Player> ignorePlayers) throws IOException {
		Writer fileWriter = new FileWriter(new File(getOutputDir(),
				getRatingSystem(type).idString() + ".csv"));
			// one separate file for each RatingSystem   
		
		OutputBuilder csvOutputBuilder = new ValidatingOutputBuilder(
				new DirectOutputBuilder(new CSVRatingsWriter(fileWriter), type));

		OutputBuilder outputBuilder = new CachingOutputBuilder(
				csvOutputBuilder, ignorePlayers);
		
		outputBuilder.initialize(null);
		
		addOutputBuilder(type, outputBuilder);
	}
	
	/** Convenience method for adding a Gnuplot Output Builder to a rating system. 
	 * @param ignorePlayers */
	public void addGnuplotOutputBuilder(RatingSystemType type, Set<Player> ignorePlayers) throws IOException {
		Writer fileWriter = new FileWriter(new File(getOutputDir(),
				getRatingSystem(type).idString() + ".dat"));
			// one separate file for each RatingSystem   
		
		OutputBuilder gnuplotOutputBuilder = new ValidatingOutputBuilder(
				new DirectOutputBuilder(new GnuPlotRatingsWriter(fileWriter), type));
		
		OutputBuilder outputBuilder = new CachingOutputBuilder(
				gnuplotOutputBuilder, ignorePlayers);

		outputBuilder.initialize(null);
		
		addOutputBuilder(type, outputBuilder);
	}
	
	public void addHtmlOutputBuilder(RatingSystemType type, Set<Player> ignorePlayers) throws IOException {
		Writer fileWriter = new FileWriter(new File(getOutputDir(),
				getRatingSystem(type).idString() + ".html"));
			// one separate file for each RatingSystem   
		
		OutputBuilder htmlOutputBuilder = new ValidatingOutputBuilder(
				new HtmlOutputBuilder(fileWriter, type));
		
		OutputBuilder outputBuilder = new CachingOutputBuilder(
				htmlOutputBuilder, ignorePlayers);

		outputBuilder.initialize(null);
		
		addOutputBuilder(type, outputBuilder);
		
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

	public void run() throws IOException, RatingException {
		// initialize the ratings
		if (previousRatings != null) {
			for (RatingStrategy ratingSystem : ratingSystems.values()) {
				RatingSystemType type = ratingSystem.getType();
				RatingFactory.initializeRatings(type, previousRatings);
			}
		}
		
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
	

	public void setMatchReader(MatchReader matchReader) {
		this.matchReader = matchReader;
	}

	public void setPreviousRatings(File previousRatings) {
		this.previousRatings = previousRatings;
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
