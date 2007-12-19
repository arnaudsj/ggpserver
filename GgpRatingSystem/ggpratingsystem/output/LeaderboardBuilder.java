package ggpratingsystem.output;

import ggpratingsystem.AbstractRating;
import ggpratingsystem.MatchSet;

/**
 * Builds a leaderboard -- i.e., an output of the development of the players'
 * ratings over time. This can be a file or a graphical representation.
 * 
 * The order in which the following methods have to be used is the following:
 * 1. new LeaderboardBuilder()
 * 2. repeat 0-* times: 
 * 		a. beginMatchSet
 * 		b. repeat 0-* times:
 * 			i. ratingUpdate
 * 		c. endMatchSet
 * 3. finish()
 * 
 * @author martin
 *
 */
public interface LeaderboardBuilder {

	/**
	 * Tells the Builder that a new MatchSet starts. This means that the old
	 * MatchSet must have been ended via endMatchSet().
	 * 
	 * @param matchSet
	 */
	public abstract void beginMatchSet(MatchSet matchSet);

	/**
	 * Tells the Builder that the currently running MatchSet is over.
	 * beginMatchSet() must have been called before. matchSet must be
	 * the same used in beginMatchSet.
	 * 
	 * @param matchSet
	 */
	public abstract void endMatchSet(MatchSet matchSet);

	/**
	 * Notifies the Builder of a new rating update. Means that one must be 
	 * between begin and end of a MatchSet.
	 *   
	 * @param rating
	 */
	public abstract void ratingUpdate(AbstractRating rating);

	/**
	 * Finishes output of this Builder. Calling any other methods is illegal.
	 */
	public abstract void finish();

}