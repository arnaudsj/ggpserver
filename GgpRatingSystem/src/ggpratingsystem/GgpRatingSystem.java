package ggpratingsystem;

import ggpratingsystem.ratingsystems.RatingException;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.martiansoftware.jsap.JSAPException;

/**
 * @author martin
 * 
 */
public class GgpRatingSystem {
	private static final Logger log = Logger.getLogger(GgpRatingSystem.class.getName());

	public static void main(String[] args) {
		try {
			CommandLineInterface.main(args);
		} catch (JSAPException e) {
			// simply drop all JSAPExceptions (they already have generated output)
		} catch (IOException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			System.err.println("Fatal error, exiting!");
		} catch (RatingException e) {
			log.log(Level.SEVERE, e.getMessage(), e);
			System.err.println("Fatal error, exiting!");
		}
	}
}
