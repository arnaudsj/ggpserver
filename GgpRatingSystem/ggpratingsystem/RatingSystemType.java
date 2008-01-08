package ggpratingsystem;

public enum RatingSystemType {
//	ELO,
//	GLICKO,
//	GLICKO_2,
//	TRUE_SKILL,
//	DIRECT,
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
	 * - a new option in CommandLineInterface
	 * - add the new RatingStrategy to the known rating strategies in the GgpRatingSystem.main()
	 */
}
