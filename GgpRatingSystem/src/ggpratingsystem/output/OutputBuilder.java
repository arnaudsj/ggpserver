package ggpratingsystem.output;

import java.io.IOException;
import java.util.List;

import ggpratingsystem.MatchSet;
import ggpratingsystem.Player;
import ggpratingsystem.ratingsystems.Rating;

/**
 * Builds a leaderboard -- i.e., an output of the development of the players'
 * ratings over time. This can be a file or a graphical representation.
 * 
 * The order in which the following methods have to be used is the following:
 * 1. initialize()
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
public interface OutputBuilder {

	/**
	 * Initializes the output of this builder, notifies it of the list of 
	 * all players.
	 * 
	 * @param players 
	 * 		the List of all players that will occur in the match set
	 * @throws IOException
	 * 		if something goes wrong
	 */
	public abstract void initialize(List<Player> players) throws IOException;
	
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
	 * Notifies the Builder of a new rating update. Must only be called 
	 * between begin and end of a MatchSet.
	 *   
	 * @param rating
	 */
	public abstract void ratingUpdate(Rating rating);

	/**
	 * Finishes output of this Builder. Calling any more methods after this
	 * has been called is illegal.
	 * @throws IOException 
	 */
	public abstract void finish();

}