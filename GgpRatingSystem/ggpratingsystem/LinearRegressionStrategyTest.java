package ggpratingsystem;

import static ggpratingsystem.RatingSystemType.LINEAR_REGRESSION;
import junit.framework.TestCase;

public class LinearRegressionStrategyTest extends TestCase {
	private LinearRegressionStrategy strategy;

	public LinearRegressionStrategyTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
		strategy = LinearRegressionStrategy.getInstance();
	}

	public void testGetType() {
		assertEquals(LINEAR_REGRESSION, strategy.getType());
	}

//	public void testUpdate() {
//		fail("Not yet implemented"); // TODO implement this test
//	}

	public void testGetInstance() {
		assertNotNull(LinearRegressionStrategy.getInstance());
		assertEquals(strategy, LinearRegressionStrategy.getInstance());
	}

}
