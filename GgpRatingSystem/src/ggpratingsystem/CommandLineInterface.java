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


import ggpratingsystem.ratingsystems.ConstantLinearRegressionStrategy;
import ggpratingsystem.ratingsystems.DirectScoresStrategy;
import ggpratingsystem.ratingsystems.DynamicLinearRegressionStrategy;
import ggpratingsystem.ratingsystems.RatingException;
import ggpratingsystem.ratingsystems.RatingSystemType;

import java.io.IOException;
import java.util.Set;
import java.util.logging.Level;

import com.martiansoftware.jsap.CommandLineTokenizer;
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAPException;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;
import com.martiansoftware.jsap.Switch;
import com.martiansoftware.jsap.UnspecifiedParameterException;
import com.martiansoftware.jsap.stringparsers.EnumeratedStringParser;
import com.martiansoftware.jsap.stringparsers.FileStringParser;

public class CommandLineInterface extends SimpleJSAP {
	public static final String APPLICATION_CALL = "ggp_rating_system.sh";
	public static final String OPTION_INPUT_DIR = "input-dir";
	public static final String OPTION_OUTPUT_DIR = "output-dir";
	public static final String OPTION_PREVIOUS_RATINGS = "previous";
	
	public static final String OPTION_DYNAMIC_LINEAR_REGRESSION = "dynamic-linear-regression-rating";
	public static final String OPTION_CONSTANT_LINEAR_REGRESSION = "constant-linear-regression-rating";
	public static final String OPTION_DIRECT_SCORES = "direct-scores-rating";
	
	public static final String OPTION_CSV_OUTPUT = "csv-output";
	public static final String OPTION_GNUPLOT_OUTPUT = "gnuplot-output";
	public static final String OPTION_HTML_OUTPUT = "html-output";
	
	public static final String OPTION_DEBUG_LEVEL = "debug-level";
	public static final String OPTION_HELP = "help";
	
	private boolean messagePrinted = false;
	
	public CommandLineInterface() throws JSAPException {
		super(
            APPLICATION_CALL, 
            "Reads a list of General Game Playing XML match files from the given "
            	+ "input directory, executes the selected rating algorithms on "
            	+ "the matches and writes the resulting player ratings to files "
            	+ "in the output directory.",
            new Parameter[] {
            /* Input directory */
                	// --input-dir, -i
                    new FlaggedOption(
    						OPTION_INPUT_DIR,
    						FileStringParser.getParser().setMustBeDirectory(true).setMustExist(true),
    						NO_DEFAULT,
    						REQUIRED,
    						'i',
    						OPTION_INPUT_DIR,
    						"The directory to read matches from. Must contain a file called "
    								+ "match_index.csv as well as the match XML files."),
                    // Checking readability of match_index.csv and XML files is done by
    				// MatchReader, which is fine (they have to be readable by the
    				// time MatchReader needs them; checking them now would not help
    				// much).
            
            /* Output directory */
                	// --output-dir, -o
                    new FlaggedOption(
    						OPTION_OUTPUT_DIR,
    						FileStringParser.getParser().setMustBeDirectory(true).setMustExist(false),
    						NO_DEFAULT,
    						REQUIRED,
    						'o',
    						OPTION_OUTPUT_DIR,
    						"The directory to write output files to."),
    				
            /* Previous rating file */
        	// --previous, -p
            new FlaggedOption(
					OPTION_PREVIOUS_RATINGS,
					FileStringParser.getParser().setMustBeFile(true).setMustExist(true),
					NO_DEFAULT,
					NOT_REQUIRED,
					'p',
					OPTION_PREVIOUS_RATINGS,
					"The CSV output file of the previous competition, if the previous ratings are to be used to initialize the new ratings."),
			
            /* Rating algorithm selection */                    
            		// --dynamic-linear-regression-rating, -d
    				new FlaggedOption(
    						OPTION_DYNAMIC_LINEAR_REGRESSION,
    						INTEGER_PARSER,
    						NO_DEFAULT,
    						NOT_REQUIRED,
    						'd',
    						OPTION_DYNAMIC_LINEAR_REGRESSION,
    						"Enables the linear regression rating algorithm, using a dynamic learning rate. " +
    						"Expects an integer value that is bigger or equal to the maximum number of " +
    						"MatchSets that will be read."),
    						
    				// --constant-linear-regression-rating, -c
    				new FlaggedOption(
    						OPTION_CONSTANT_LINEAR_REGRESSION,
    						DOUBLE_PARSER,
    						NO_DEFAULT,
    						NOT_REQUIRED,
    						'c',
    						OPTION_CONSTANT_LINEAR_REGRESSION,
    						"Enables the linear regression rating algorithm, using a constant learning rate. " +
    						"Expects a double value specifying the learning rate to be used (e.g. 1.0)."),
                    
    				// --direct-scores-rating, -s
    				new Switch(
    						OPTION_DIRECT_SCORES,
    						's',
    						OPTION_DIRECT_SCORES,
    						"Enables the direct scores rating algorithm. This is not really a rating algorithm, " +
    						"but rather sums up the scores received by the players."),

					/* ****************** ADD NEW RATING SYSTEMS HERE ****************** */
            		
            /* Output selection */
    				// --csv-output, -v
    				new Switch(
    						OPTION_CSV_OUTPUT,
    						'v',
    						OPTION_CSV_OUTPUT,
    						"Enables CSV (comma separated values) output for all rating algorithms."),

    				// --gnuplot-output, -g
    				new Switch(
    						OPTION_GNUPLOT_OUTPUT,
    						'g',
    						OPTION_GNUPLOT_OUTPUT,
    						"Enables gnuplot (data file) output for all rating algorithms."),

    				// --html-output, -t
    				new Switch(
    						OPTION_HTML_OUTPUT,
    						't',
    						OPTION_HTML_OUTPUT,
    						"Enables HTML output for all rating algorithms."),

					/* ****************** ADD NEW OUTPUT METHODS HERE ****************** */

    		/* Debug level */
            	// --debug-level, -l
                    new FlaggedOption(
                    		OPTION_DEBUG_LEVEL,
                    		EnumeratedStringParser.getParser(
                    				"OFF; SEVERE; WARNING; INFO; CONFIG; FINE; FINER; FINEST; ALL",
                    				false, false),
                    		"INFO",
                    		NOT_REQUIRED,
                    		'l',
                    		OPTION_DEBUG_LEVEL,
                    		"Sets the level of debug output. One of the following (in order of "
                    			+ "increasing verbosity): "
                    			+ "OFF; SEVERE; WARNING; INFO; CONFIG; FINE; FINER; FINEST; ALL. "
                    			+ "If the debug level is at FINE or higher, detailed statistical "
                   				+ "information about the linear regression will be written to the "
                   				+ "output directory.")
                    
            /* Help */
            	// --help option is added by SimpleJSAP 
            }
        );
	}

	@Override
	public boolean messagePrinted() {
		return messagePrinted;
	}

	@Override
	public JSAPResult parse(String cmdLine) {
        String[] args = CommandLineTokenizer.tokenize(cmdLine);
        return (parse(args));
	}

	@Override
	public JSAPResult parse(String[] args) {
        JSAPResult config;
        config = super.parse(args);

		try {
			/* parameter checking */
			this.messagePrinted = super.messagePrinted();
			
			boolean existsEnabledRatingAlgorithm = 
				config.contains(OPTION_DYNAMIC_LINEAR_REGRESSION)
				|| config.contains(OPTION_CONSTANT_LINEAR_REGRESSION)
				|| config.getBoolean(OPTION_DIRECT_SCORES);
				/* ****************** ADD NEW RATING SYSTEMS HERE ****************** */
			
			if (!existsEnabledRatingAlgorithm) {
				messagePrinted = true;
				if (!config.getBoolean(OPTION_HELP)) {
					System.err.println("Error: At least one rating algorithm must be enabled.");
				}
			}
			
			boolean existsEnabledOutputAlgorithm = 
				config.getBoolean(OPTION_CSV_OUTPUT) 
				|| config.getBoolean(OPTION_GNUPLOT_OUTPUT)
				|| config.getBoolean(OPTION_HTML_OUTPUT);
				/* ****************** ADD NEW OUTPUT METHODS HERE ****************** */

			if (!existsEnabledOutputAlgorithm) {
				messagePrinted = true;
				if (!config.getBoolean(OPTION_HELP)) {
					System.err.println("Error: At least one output algorithm must be enabled.");
				}
			}
			
			if (messagePrinted) {
				// if user hasn't asked for help, "beat him with a clue stick", as the JSAP manual says
				if (!config.getBoolean(OPTION_HELP)) {
					System.err.println();
					System.err.println("Type " + APPLICATION_CALL + " --" + OPTION_HELP + " for help.");
				}
				config.addException(null, new JSAPException("Help message printed."));
			}
		} catch (UnspecifiedParameterException e) {
			if (!config.getBoolean(OPTION_HELP)) {
				System.err.println();
				System.err.println("Type " + APPLICATION_CALL + " --" + OPTION_HELP + " for help.");
			}
			throw e;
		}
		return config;
	}
	
	public static void main(String[] args) throws JSAPException, IOException, RatingException {
		CommandLineInterface commandLineInterface = new CommandLineInterface();
        
		JSAPResult jsap = commandLineInterface.parse(args);
		if (!jsap.success()) {
			throw new JSAPException("Command line parsing failed.");
		}
        
        //////////////////////////////////////////////////////		
		//             now configure everything             //
        //////////////////////////////////////////////////////		
		Configuration configuration = new Configuration();
		
		/* configure debug level */
		String debugLevel = jsap.getString(OPTION_DEBUG_LEVEL).toUpperCase();
		Level level = Level.parse(debugLevel);
		configuration.setDebugLevel(level);
        
		/* configure input dir */
		MatchSetReader matchSetReader = new FileMatchSetReader(jsap.getFile(OPTION_INPUT_DIR), configuration);
		configuration.setMatchReader(matchSetReader);
		
		/* configure output dir */
		Configuration.setOutputDir(jsap.getFile(OPTION_OUTPUT_DIR));
		
		/* configure previous ratings file */
		configuration.setPreviousRatings(jsap.getFile(OPTION_PREVIOUS_RATINGS));

		/* configure rating algorithms */
		if (jsap.contains(OPTION_DYNAMIC_LINEAR_REGRESSION)) {
			int maxMatchSets = jsap.getInt(OPTION_DYNAMIC_LINEAR_REGRESSION);
			configuration.addRatingSystem(new DynamicLinearRegressionStrategy(maxMatchSets));
		}
		if (jsap.contains(OPTION_CONSTANT_LINEAR_REGRESSION)) {
			double learningRate = jsap.getDouble(OPTION_CONSTANT_LINEAR_REGRESSION);
			configuration.addRatingSystem(new ConstantLinearRegressionStrategy(learningRate));
		}
		if (jsap.getBoolean(OPTION_DIRECT_SCORES)) {
			configuration.addRatingSystem(new DirectScoresStrategy());
		}
		/* ****************** ADD NEW RATING SYSTEMS HERE ****************** */
		

		/* make ignore list */
		Set<Player> ignorePlayers = new IgnorePlayerSet(jsap.getFile(OPTION_INPUT_DIR), configuration.getPlayerSet());
		
		/* configure output methods */
		for (RatingSystemType type : configuration.getEnabledRatingSystems()) {
			if (jsap.getBoolean(OPTION_CSV_OUTPUT)) {
				configuration.addCSVOutputBuilder(type, ignorePlayers);
			}
			if (jsap.getBoolean(OPTION_GNUPLOT_OUTPUT)) {
				configuration.addGnuplotOutputBuilder(type, ignorePlayers);
			}
			if (jsap.getBoolean(OPTION_HTML_OUTPUT)) {
				configuration.addHtmlOutputBuilder(type, ignorePlayers);
			}
			/* ****************** ADD NEW OUTPUT METHODS HERE ****************** */
		}

		configuration.run();
	}
}