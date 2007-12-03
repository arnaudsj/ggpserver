package ggpratingsystem;

import java.util.HashMap;
import java.util.List;

public class Game {
	private final String name;
	private List<String> roles;	// TODO: This should be final
//	private final List<List<Integer>> teams;
	
	private static HashMap<String, Game> instances = new HashMap<String, Game>();
	
	private Game(String name) {
		super();
		this.name = name;
	}
	
	public static Game getInstance(String name) {
		Game result = instances.get(name);
		
		if (result == null) {
			result = new Game(name);
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

	public void setRoles(List<String> roles) {	// TODO: remove
		this.roles = roles;
	}
}