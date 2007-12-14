package ggpratingsystem;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class Player {
	private static final Logger log = Logger.getLogger(Player.class.getName());
    
	private static Map<String, Player> instances = new HashMap<String, Player>();

	private final String name;
	private Map<RatingSystemType, AbstractRating> ratings = new HashMap<RatingSystemType, AbstractRating>();
	
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

	public AbstractRating getRating(RatingSystemType type) {
		AbstractRating result = ratings.get(type);
		
		if (result == null) {
			switch (type) {
			case LINEAR_REGRESSION:
				result = new LinearRegressionRating();
				break;			
			/* all new subclasses of AbstractRating have to be added here */
				
			default:
				throw new IllegalArgumentException("unknown RatingSystemType!");
			}
			
			putRating(result);
		}
		
		return result;
	}

	private void putRating(AbstractRating rating) {
		RatingSystemType type = rating.getType();
		
		if (ratings.get(type) != null)
			log.fine("Warning: Rating overwritten!");
		ratings.put(type, rating);
	}

	@Override
	public String toString() {
		return getName();
	}
}