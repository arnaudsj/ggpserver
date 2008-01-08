package ggpratingsystem;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for ggpratingsystem");
		//$JUnit-BEGIN$
		suite.addTestSuite(MatchSetTest.class);
		suite.addTestSuite(LinearRegressionGameInfoTest.class);
		suite.addTestSuite(MatchReaderTest.class);
		suite.addTestSuite(GgpRatingSystemTest.class);
		suite.addTestSuite(MatchTest.class);
		suite.addTestSuite(LinearRegressionStrategyTest.class);
		suite.addTestSuite(LinearRegressionRatingTest.class);
		//$JUnit-END$
		return suite;
	}

}
