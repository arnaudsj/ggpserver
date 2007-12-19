package ggpratingsystem.output;

import ggpratingsystem.AbstractRating;
import ggpratingsystem.MatchSet;

/**
 * Implementation of LeaderboardBuilder which validates the constraints on the method call order.
 * Can be used as a decorator for other LeaderboardBuilders.
 * 
 * @author martin
 *
 */
public class ValidatingLeaderboardBuilder implements LeaderboardBuilder {
	private final LeaderboardBuilder decorated;
	private boolean finished = false;
	private MatchSet currentMatchSet;
	
	public ValidatingLeaderboardBuilder(LeaderboardBuilder decorated) {
		super();
		if (decorated == null) {
			throw new IllegalArgumentException("decorated may not be null!");
		}
		this.decorated = decorated;
	}

	/* (non-Javadoc)
	 * @see ggpratingsystem.output.LeaderboardBuilder#beginMatchSet(ggpratingsystem.MatchSet)
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
	 * @see ggpratingsystem.output.LeaderboardBuilder#endMatchSet(ggpratingsystem.MatchSet)
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
	 * @see ggpratingsystem.output.LeaderboardBuilder#ratingUpdate(ggpratingsystem.AbstractRating)
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
	 * @see ggpratingsystem.output.LeaderboardBuilder#finish()
	 */
	public void finish() {
		if (currentMatchSet != null) {
			throw new IllegalStateException("finish() was called before calling endMatchSet()!");
		}
		this.finished  = true;
		
		decorated.finish();
	}
}
