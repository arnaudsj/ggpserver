/*
    Copyright (C) 2008,2009 Martin Günther <mintar@gmx.de>

    This file is part of GgpRatingSystem.

    GgpRatingSystem is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GgpRatingSystem is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GgpRatingSystem.  If not, see <http://www.gnu.org/licenses/>.
*/

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
		if (roles == null) {
			throw new IllegalStateException("Game.getRoles() called before setRoles()!");
		}
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