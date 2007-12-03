package ggpratingsystem;

/**
 * Singleton RatingStrategy
 * 
 * @author martin
 *
 */
public abstract class RatingStrategy {
	public abstract RatingType getType();

	public abstract void updateSkills(MatchSet matches);

	public abstract RatingStrategy getInstance();
}