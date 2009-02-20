package ggpratingsystem;

import ggpratingsystem.util.Util;

import java.io.File;
import java.io.IOException;

import junit.framework.TestCase;

public class FileMatchReaderTest extends TestCase {

	/*
	 * Test method for 'ggpratingsystem.MatchReader.readMatches(String)'
	 */
	public void testReadMatches() throws IOException {
		MatchReader matchReader = new FileMatchReader(new File(Util.getDataDir(), "competition2007" + File.separator + "xml"));
		
		int numMatchSets = 0;
		while (matchReader.hasNext()) {
			numMatchSets++;
			matchReader.readMatchSet();
		}
		assertEquals(47, numMatchSets);
	}
}
