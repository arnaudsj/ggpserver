package tud.ggpserver.scheduler;

import java.util.HashMap;
import java.util.Map;

public class GameProperties {
	private final int startClock;
	private final int playClock;
	private final String stylesheet;
	
	private static Map<String, GameProperties> instances;
	private static GameProperties defaultInstance;
	
	static {
		instances = new HashMap<String, GameProperties>();
//		instances.put("amazons", new GameProperties(1800, 10));
		instances.put("amazons", new GameProperties(180, 10));
		instances.put("asteroids", new GameProperties(600, 30));
//		instances.put("8puzzle", new GameProperties(60, 10));
		instances.put("8puzzle", new GameProperties(5, 5));
		instances.put("blockerparallel", new GameProperties(60, 10));
		instances.put("checkers", new GameProperties(60, 10));
		instances.put("chess", new GameProperties(240, 10));
		instances.put("wargame", new GameProperties(120, 10));
		instances.put("friendgame", new GameProperties(120, 10));
		instances.put("melee", new GameProperties(30, 10));
		instances.put("crossers3", new GameProperties(20, 10));
		instances.put("farmers", new GameProperties(120, 10));
		instances.put("guess", new GameProperties(60, 10));
		instances.put("othello", new GameProperties(120, 10));
		instances.put("pentago", new GameProperties(60, 10));
		instances.put("ronaldinho", new GameProperties(120, 10));
		instances.put("skirmish", new GameProperties(120, 10));
//		instances.put("skirmish2", new GameProperties(30, 10));
//		instances.put("skirmish3", new GameProperties(30, 10));
		instances.put("tictactoeparallel", new GameProperties(30, 10));
		instances.put("pacman3p", new GameProperties(60, 10));
		instances.put("wallmaze", new GameProperties(10, 5));
		
		defaultInstance = new GameProperties(180, 30);
	}

	public static GameProperties getInstance(String gameName) {
		for (String name : instances.keySet()) {
			if (gameName.contains(name)) {
				return instances.get(name);
			}
		}
		return defaultInstance;
	}
	
	protected GameProperties(final int startClock, final int playClock) {
		this(startClock, playClock, "../stylesheets/generic/generic.xsl");
	}

	protected GameProperties(final int startClock, final int playClock, String stylesheet) {
		this.startClock = startClock;
		this.playClock = playClock;
		this.stylesheet = stylesheet;
	}

	public int getPlayClock() {
		return playClock;
	}

	public int getStartClock() {
		return startClock;
	}

	public String getStylesheet() {
		return stylesheet;
	}
}
