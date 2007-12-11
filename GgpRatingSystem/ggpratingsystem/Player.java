package ggpratingsystem;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class Player {
	private static final Logger log = Logger.getLogger(Player.class.getName());
    
	private static Map<String, Player> instances = new HashMap<String, Player>();

	private final String name;
	private Map<RatingType, AbstractRating> ratings = new HashMap<RatingType, AbstractRating>();
	
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

	public AbstractRating getRating(RatingType type) {
		AbstractRating result = ratings.get(type);
		
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

	private void putRating(AbstractRating rating) {
		RatingType type = rating.getType();
		
		if (ratings.get(type) != null)
			log.fine("Warning: Rating overwritten!");
		ratings.put(type, rating);
	}	
}