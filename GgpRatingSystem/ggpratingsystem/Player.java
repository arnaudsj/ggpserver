package ggpratingsystem;

import java.util.HashMap;
import java.util.Map;

public class Player {

	private static Map<String, Player> instances = new HashMap<String, Player>();

	private final String name;
	private Map<RatingType, Rating> ratings = new HashMap<RatingType, Rating>();
	
	private Player(String name) {
		super();
		this.name = name;
		
		if (instances.get(name) != null)
			throw new IllegalStateException("Player already exists!");
		
		instances.put(name, this);
	}

	public static Player getInstance(String playerName) {
		Player result = instances.get(playerName);		
		if (result == null)
			result = new Player(playerName);
		
		return result;
	}

	public String getName() {
		return name;
	}

	public Rating getRating(RatingType type) {
		Rating result = ratings.get(type);
		
		if (result == null) {
			switch (type) {
			case ELO:
				result = new EloRating();
				break;			
			// TODO: add other cases
				
			default:
				throw new IllegalArgumentException("unknown RatingType!");
			}
			
			putRating(result);
		}
		
		return result;
	}

	private void putRating(Rating rating) {
		RatingType type = rating.getType();
		
		if (ratings.get(type) != null)
			System.err.println("Warning: Rating overwritten!");
		ratings.put(type, rating);
	}	
}