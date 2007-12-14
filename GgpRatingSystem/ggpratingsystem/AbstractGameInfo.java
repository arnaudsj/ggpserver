package ggpratingsystem;

public abstract class AbstractGameInfo {
	private final Game game;

	public AbstractGameInfo(final Game game) {
		super();
		this.game = game;
	}

	public abstract RatingSystemType getType();

	public Game getGame() {
		return game;
	}
	
	public abstract void updateGameInfo(MatchSet matches);

	/**
	 * Resets the game info to the default values.
	 */
	public abstract void reset();
	
}
