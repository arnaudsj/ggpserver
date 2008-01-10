package ggpratingsystem.ratingsystems;

import ggpratingsystem.Game;
import ggpratingsystem.MatchSet;

public abstract class AbstractGameInfo {
	private final Game game;

	/**
     * @deprecated Use {@link ggpratingsystem.ratingsystems.GameInfoFactory#makeGameInfo(RatingSystemType, Game)} instead.
	 */
	@Deprecated
	protected AbstractGameInfo(final Game game) {
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
