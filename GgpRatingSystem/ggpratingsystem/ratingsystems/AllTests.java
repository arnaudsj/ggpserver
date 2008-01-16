package ggpratingsystem.ratingsystems;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(
				"Test for ggpratingsystem.ratingsystems");
		//$JUnit-BEGIN$
		suite.addTestSuite(RatingFactoryTest.class);
		suite.addTestSuite(GameInfoFactoryTest.class);
		suite.addTestSuite(LinearRegressionGameInfoTest.class);
		//$JUnit-END$
		return suite;
	}

}
