package ggpratingsystem;

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
			switch (type) {
			case LINEAR_REGRESSION:
				result = new LinearRegressionGameInfo(this);
				break;			
			/* all new subclasses of AbstractGameInfo have to be added here */
				
			default:
				throw new IllegalArgumentException("unknown RatingSystemType!");
			}
			
			putGameInfo(result);
		}
		
		return result;
	}

	private void putGameInfo(AbstractGameInfo gameInfo) {
		RatingSystemType type = gameInfo.getType();
		
		if (gameInfos.get(type) != null)
			log.fine("Warning: GameInfo overwritten!");
		gameInfos.put(type, gameInfo);
	}
}