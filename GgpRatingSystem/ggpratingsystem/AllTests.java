package ggpratingsystem;

import ggpratingsystem.ratingsystems.GameInfoFactoryTest;
import ggpratingsystem.ratingsystems.LinearRegressionGameInfoTest;
import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite("Test for ggpratingsystem");
		//$JUnit-BEGIN$
		suite.addTestSuite(MatchSetTest.class);
		suite.addTestSuite(GameInfoFactoryTest.class);
		suite.addTestSuite(LinearRegressionGameInfoTest.class);
		suite.addTestSuite(MatchReaderTest.class);
		suite.addTestSuite(GgpRatingSystemTest.class);
		suite.addTestSuite(MatchTest.class);
		//$JUnit-END$
		return suite;
	}

}
