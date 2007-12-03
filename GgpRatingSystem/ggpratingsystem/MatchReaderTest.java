package ggpratingsystem;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

public class MatchReaderTest extends TestCase {

	/*
	 * Test method for 'ggpratingsystem.MatchReader.readMatches(String)'
	 */
	public void testReadMatches() throws IOException {
		String userdir = System.getProperty("user.dir");
		if (!userdir.endsWith(File.separator))
			userdir += File.separator;

//		String directory = userdir + "data" + File.separator
//				+ "2007_final_round";
		String directory = userdir + "data" + File.separator
				+ "2007_preliminaries";
		
		List<MatchSet> matchSets = MatchReader.readMatches(directory);
		
		for (MatchSet set : matchSets) {
			System.out.println(set.toString());
		}
	}
}
