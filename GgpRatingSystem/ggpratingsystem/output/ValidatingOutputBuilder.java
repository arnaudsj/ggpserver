package ggpratingsystem.output;

import java.io.IOException;

import ggpratingsystem.AbstractRating;
import ggpratingsystem.MatchSet;

/**
 * Implementation of OutputBuilder which validates the constraints on the method call order.
 * Can be used as a decorator for other OutputBuilders.
 * 
 * @author martin
 *
 */
public class ValidatingOutputBuilder implements OutputBuilder {
	private final OutputBuilder decorated;
	private boolean finished = false;
	private MatchSet currentMatchSet;
	
	public ValidatingOutputBuilder(OutputBuilder decorated) {
		super();
		if (decorated == null) {
			throw new IllegalArgumentException("decorated may not be null!");
		}
		this.decorated = decorated;
	}

	/* (non-Javadoc)
	 * @see ggpratingsystem.output.OutputBuilder#beginMatchSet(ggpratingsystem.MatchSet)
	 */
	public void beginMatchSet(MatchSet matchSet) {
		if (finished) {
			throw new IllegalStateException("finished() has been called before!");
		}
		if (currentMatchSet != null) {
			throw new IllegalStateException("Old MatchSet has not been ended with endMatchSet() before calling beginMatchSet()!");
		}
		currentMatchSet = matchSet;
		
		decorated.beginMatchSet(matchSet);
	}
	
	/* (non-Javadoc)
	 * @see ggpratingsystem.output.OutputBuilder#endMatchSet(ggpratingsystem.MatchSet)
	 */
	public void endMatchSet(MatchSet matchSet) {
		if (finished) {
			throw new IllegalStateException("finished() has been called before!");
		}
		if (currentMatchSet == null) {
			throw new IllegalStateException("endMatchSet() was called before calling beginMatchSet()!");
		}
		if (!currentMatchSet.equals(matchSet)) {
			throw new IllegalStateException("endMatchSet() was called with a different argument than beginMatchSet()!");
		}
		currentMatchSet = null;
		
		decorated.endMatchSet(matchSet);
	}
	
	/* (non-Javadoc)
	 * @see ggpratingsystem.output.OutputBuilder#ratingUpdate(ggpratingsystem.AbstractRating)
	 */
	public void ratingUpdate(AbstractRating rating) {
		if (finished) {
			throw new IllegalStateException("finished() has been called before!");
		}
		if (currentMatchSet == null) {
			throw new IllegalStateException("ratingUpdate() was called before calling beginMatchSet()!");
		}
		
		decorated.ratingUpdate(rating);
	}
	
	/* (non-Javadoc)
	 * @see ggpratingsystem.output.OutputBuilder#finish()
	 */
	public void finish() throws IOException {
		if (currentMatchSet != null) {
			throw new IllegalStateException("finish() was called before calling endMatchSet()!");
		}
		this.finished  = true;
		
		decorated.finish();
	}
}
