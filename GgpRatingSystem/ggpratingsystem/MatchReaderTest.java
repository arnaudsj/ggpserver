package ggpratingsystem;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

public class MatchReaderTest extends TestCase {

	/*
	 * Test method for 'ggpratingsystem.MatchReader.readMatches(String)'
	 */
	public void testReadMatchesPreliminaries() throws IOException {
		List<MatchSet> matchSets = readSubdir("2007_preliminaries");
		assertEquals(matchSets.size(), 43);
		for (MatchSet set : matchSets) {
			System.out.println(set.toString());
		}
	}

	/*
	 * Test method for 'ggpratingsystem.MatchReader.readMatches(String)'
	 */
	public void testReadMatchesFinals() throws IOException {
		List<MatchSet> matchSets = readSubdir("2007_final_round");
		assertEquals(matchSets.size(), 4);
		for (MatchSet set : matchSets) {
			System.out.println(set.toString());
		}
	}

	public static List<MatchSet> readSubdir(String subdirectory) throws IOException {
		String userdir = System.getProperty("user.dir");
		if (!userdir.endsWith(File.separator))
			userdir += File.separator;

		String directory = userdir + "data" + File.separator + subdirectory;
		
		return MatchReader.readMatches(directory);
		
	}
}
