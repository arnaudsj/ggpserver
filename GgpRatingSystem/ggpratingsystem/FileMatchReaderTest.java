package ggpratingsystem;

import ggpratingsystem.util.Util;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class FileMatchReaderTest extends TestCase {

	/*
	 * Test method for 'ggpratingsystem.MatchReader.readMatches(String)'
	 */
	public void testReadMatchesPreliminaries() throws IOException {
		MatchReader matchReader = new FileMatchReader(new File(Util.getDataDir(), "2007_preliminaries"));
		
		int numMatchSets = 0;
		while (matchReader.hasNext()) {
			numMatchSets++;
			matchReader.readMatchSet();
		}
		assertEquals(43, numMatchSets);
	}

	/*
	 * Test method for 'ggpratingsystem.MatchReader.readMatches(String)'
	 */
	public void testReadMatchesFinals() throws IOException {
		MatchReader matchReader = new FileMatchReader(new File(Util.getDataDir(), "2007_final_round"));
		
		int numMatchSets = 0;
		while (matchReader.hasNext()) {
			numMatchSets++;
			matchReader.readMatchSet();
//			System.out.println(matchReader.readMatchSet().toString());
		}
		assertEquals(4, numMatchSets);
	}
}
