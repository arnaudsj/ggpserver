package ggpratingsystem;

public enum RatingSystemType {
//	ELO,
//	GLICKO,
//	GLICKO_2,
//	TRUE_SKILL,
	LINEAR_REGRESSION;
	
	/*
	 * Whenever a new RatingSystemType is created, the following have to be
	 * provided for this type:
	 * 
	 * - a new subclass of AbstractGameInfo
	 * - a corresponding entry in Game.getGameInfo() 
	 * - a new subclass of AbstractRating
	 * - a corresponding entry in Player.getRating()
	 * - a new subclass of AbstractRatingStrategy
	 * - TODO: add this somewhere in the main method
	 */
}
