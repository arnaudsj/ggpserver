package ggpratingsystem;

import ggpratingsystem.ratingsystems.AbstractGameInfo;
import ggpratingsystem.ratingsystems.GameInfoFactory;
import ggpratingsystem.ratingsystems.RatingSystemType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class Game {
	private static final Logger log = Logger.getLogger(Game.class.getName());
	private static Map<String, Game> instances = new HashMap<String, Game>();

	private final String name;
	private List<String> roles;
//	private final List<List<Integer>> teams;
	
	private Map<RatingSystemType, AbstractGameInfo> gameInfos = new HashMap<RatingSystemType, AbstractGameInfo>();
	
	static {
		// inherit default level for package ggpratingsystem
		log.setLevel(null);
	}
	
	private Game(String name) {
		super();
		this.name = name;
	}
	
	public static Game getInstance(String name) {
		Game result = instances.get(name);
		
		if (result == null) {
			result = new Game(name);
			instances.put(name, result);
		}
		
		return result;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {	// TODO What an ugly hack. Fix this in the future when roles of a game are available directly and not only via the matches.  
		this.roles = roles;
	}
	

	public AbstractGameInfo getGameInfo(RatingSystemType type) {
		AbstractGameInfo result = gameInfos.get(type);
		
		if (result == null) {
			result = GameInfoFactory.makeGameInfo(type, this);
			gameInfos.put(type, result);
		}
		
		return result;
	}
}