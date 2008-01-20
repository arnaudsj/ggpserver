package ggpratingsystem;

import ggpratingsystem.ratingsystems.Rating;
import ggpratingsystem.ratingsystems.RatingFactory;
import ggpratingsystem.ratingsystems.RatingSystemType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class Player {
	private static final Logger log = Logger.getLogger(Player.class.getName());
    
	static {
		// inherit default level for package ggpratingsystem
		log.setLevel(null);
	}
	
	private static Map<String, Player> instances = new HashMap<String, Player>();

	private final String name;
	private Map<RatingSystemType, Rating> ratings = new HashMap<RatingSystemType, Rating>();
	
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

	public Rating getRating(RatingSystemType type) {
		Rating result = ratings.get(type);
		
		if (result == null) {
			result = RatingFactory.makeRating(type, this);
			ratings.put(type, result);
		}
		
		return result;
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Player other = (Player) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public static List<Player> getAllPlayers() {
		return new LinkedList<Player>(instances.values());
	}
}