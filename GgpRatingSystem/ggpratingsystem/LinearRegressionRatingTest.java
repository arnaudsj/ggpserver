package ggpratingsystem;

import static ggpratingsystem.RatingSystemType.LINEAR_REGRESSION;
import junit.framework.TestCase;

public class LinearRegressionRatingTest extends TestCase {
	private LinearRegressionRating rating;

	public LinearRegressionRatingTest(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
		rating = new LinearRegressionRating();
	}

	public void testGetType() {
		assertEquals(LINEAR_REGRESSION, rating.getType());
	}

	public void testUpdateRatings() {
		fail("Not yet implemented"); // FIXME
	}

	public void testGetSetCurRating() {
		rating.setCurRating(1234.5);
		assertEquals(1234.5, rating.getCurRating());
	}

}
