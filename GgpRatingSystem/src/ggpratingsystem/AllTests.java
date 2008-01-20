package ggpratingsystem;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for ggpratingsystem");
		//$JUnit-BEGIN$
		suite.addTestSuite(MatchSetTest.class);
		suite.addTestSuite(FileMatchReaderTest.class);
		suite.addTestSuite(CommandLineInterfaceTest.class);
		suite.addTestSuite(MatchTest.class);
		//$JUnit-END$
		suite.addTest(ggpratingsystem.ratingsystems.AllTests.suite());
		return suite;
	}

}
