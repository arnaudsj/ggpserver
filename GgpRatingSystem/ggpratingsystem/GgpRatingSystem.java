package ggpratingsystem;

import static ggpratingsystem.RatingSystemType.LINEAR_REGRESSION;
import ggpratingsystem.output.CSVOutputBuilder;
import ggpratingsystem.output.CachingCSVOutputBuilder;
import ggpratingsystem.output.OutputBuilder;
import ggpratingsystem.output.ValidatingOutputBuilder;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;


import static ggpratingsystem.CommandLineInterface.OPTION_INPUT_DIR;
import static ggpratingsystem.CommandLineInterface.OPTION_OUTPUT_DIR;
import static ggpratingsystem.CommandLineInterface.OPTION_LINEAR_REGRESSION;
import static ggpratingsystem.CommandLineInterface.OPTION_CSV_OUTPUT;
import static ggpratingsystem.CommandLineInterface.OPTION_GNUPLOT_OUTPUT;
import static ggpratingsystem.CommandLineInterface.OPTION_DEBUG_LEVEL;


/**
 * @author martin
 *
 */
public class GgpRatingSystem {
	public static void main(String[] args) throws IOException, JSAPException {
		CommandLineInterface commandLineInterface = new CommandLineInterface();
        
		JSAPResult config = commandLineInterface.parse(args);
		if (!config.success()) {
			throw new JSAPException("Command line parsing failed.");
		}
        
        //////////////////////////////////////////////////////		
		//             now configure everything             //
        //////////////////////////////////////////////////////		
		
		/* configure debug level */
		String debugLevel = config.getString(OPTION_DEBUG_LEVEL).toUpperCase();
		Level level = Level.parse(debugLevel);
		Logger.getLogger("ggpratingsystem").setLevel(level);
        
		/* configure input dir */
		List<MatchSet> matchSets = MatchReader.readMatches(config.getFile(OPTION_INPUT_DIR));

		/* create output dir, if it does not exist */
		File outputDir = config.getFile(OPTION_OUTPUT_DIR);
		if (!outputDir.exists()) {
			outputDir.mkdirs();
		}
		
		/* configure rating algorithms */
		List<AbstractRatingStrategy> ratingStrategies = new LinkedList<AbstractRatingStrategy>();

		if (config.getBoolean(OPTION_LINEAR_REGRESSION)) {
			ratingStrategies.add(LinearRegressionStrategy.getInstance());
		}
		// add other rating strategies here 
		
		/* configure output methods */
		
		Map<AbstractRatingStrategy, List<OutputBuilder>> outputBuilders = new HashMap<AbstractRatingStrategy, List<OutputBuilder>>();

		if (config.getBoolean(OPTION_CSV_OUTPUT)) {
			for (AbstractRatingStrategy strategy : ratingStrategies) {
				Writer writer = new FileWriter(new File(outputDir, "ggp-rating-output.csv"));
					// TODO one separate file for each RatingSystem --> some getString method of getType would be great 
				List<Player> players = Player.getAllPlayers();				
				OutputBuilder outputBuilder = 
					new ValidatingOutputBuilder(
						new CSVOutputBuilder(writer, players, strategy.getType()));
				// This is already written, since we already parsed all matches above.

				// without parsing first:
//				OutputBuilder outputBuilder = 
//					new ValidatingOutputBuilder(
//						new CachingCSVOutputBuilder(writer, strategy.getType()));
				
				List<OutputBuilder> builders = new LinkedList<OutputBuilder>();
				builders.add(outputBuilder);
				outputBuilders.put(strategy, builders);
			}
		}
		// add other output methods here
		// TODO gnuplot
		// TODO should the builders be passed to the MatchReader?

		processMatches(matchSets, ratingStrategies, outputBuilders);
	}

	/**
	 * @param matchSets
	 * @param ratingStrategies
	 * @param outputBuilders
	 * @throws IOException 
	 */
	private static void processMatches(List<MatchSet> matchSets, List<AbstractRatingStrategy> ratingStrategies, Map<AbstractRatingStrategy, List<OutputBuilder>> outputBuilders) throws IOException {
		// TODO: should this be moved elsewhere? 
		for (MatchSet set : matchSets) {
			for (AbstractRatingStrategy strategy : ratingStrategies) {
				strategy.update(set);
				List<OutputBuilder> builders = outputBuilders.get(strategy);

				for (OutputBuilder builder : builders) {
					builder.beginMatchSet(set);
				}
				
				List<Match> matches = set.getMatches();
				for (Match match : matches) {
					List<Player> playersInMatch = match.getPlayers();
					
					for (Player player : playersInMatch) {
						for (OutputBuilder builder : builders) {
							builder.ratingUpdate(player.getRating(LINEAR_REGRESSION));
						}
					}
				}
				
				for (OutputBuilder builder : builders) {
					builder.endMatchSet(set);
				}
			}
		}
		
		for (AbstractRatingStrategy strategy : ratingStrategies) {
			List<OutputBuilder> builders = outputBuilders.get(strategy);
			for (OutputBuilder builder : builders) {
				builder.finish();
			}
		}
	}
}
