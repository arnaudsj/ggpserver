package ggpratingsystem;

public interface MatchReader {
	/**
	 * Reads and returns the next MatchSet.
	 */
	public abstract MatchSet readMatchSet();

	/**
	 * Indicates whether this MatchReader has a next MatchSet to read.
	 */
	public abstract boolean hasNext();
}