package ggpratingsystem;

import java.io.IOException;

import com.martiansoftware.jsap.JSAPException;

/**
 * @author martin
 *
 */
public class GgpRatingSystem {
	public static void main(String[] args) throws IOException {
		try {
			CommandLineInterface.main(args);
		} catch (JSAPException e) {
			// simply drop all JSAPExceptions (they already have generated output)
		}
	}
}
