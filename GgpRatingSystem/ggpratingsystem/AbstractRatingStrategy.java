package ggpratingsystem;

/**
 * Singleton RatingStrategy
 * 
 * @author martin
 *
 */
public abstract class AbstractRatingStrategy {
	public abstract RatingSystemType getType();

	public abstract void update(MatchSet matches);

//	public abstract static AbstractRatingStrategy getInstance();
}