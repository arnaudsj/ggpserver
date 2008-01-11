package ggpratingsystem.ratingsystems;

import ggpratingsystem.MatchSet;

/**
 *	Defines how to update GameInfos and Ratings for a complete MatchSet. 
 * 
 * @author martin
 *
 */
public interface RatingStrategy {
	
	/**
	 * @return the type of this rating strategy.
	 */
	public RatingSystemType getType();
	
	/**
	 * Update the corresponding Ratings and the GameInfos, if used.
	 */
	public void update(MatchSet matches);
	
	/**
	 * @return A String identifying this rating strategy that can be used as
	 *         a unique ID. Must not clash with other rating strategies.
	 */
	public String idString();
}