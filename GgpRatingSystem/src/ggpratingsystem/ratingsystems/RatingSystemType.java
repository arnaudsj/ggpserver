package ggpratingsystem.ratingsystems;

public enum RatingSystemType {
//	ELO,
//	GLICKO,
//	GLICKO_2,
//	TRUE_SKILL,
	CONSTANT_LINEAR_REGRESSION,
	DYNAMIC_LINEAR_REGRESSION,
	DIRECT_SCORES;
	
	/*
	 * Whenever a new RatingSystemType is created, the following have to be
	 * provided for this type:
	 * 
	 * - a new class implementing RatingStrategy, defining how to update all Ratings and GameInfos for a match set
	 * 
	 * - if needed: 
	 * 		- a new subclass of Rating, defining how to update a single rating
	 * 		- a corresponding entry in RatingFactory
	 * 
	 * - if needed:
	 * 		- a new subclass of AbstractGameInfo
	 * 		- a corresponding entry in GameInfoFactory 
	 *  
	 * - a new option in CommandLineInterface, search for:
	 * 		****************** ADD NEW RATING SYSTEMS HERE ******************
	 * 
	 * - parse the new option in CommandLineInterface.main()
	 */
}
