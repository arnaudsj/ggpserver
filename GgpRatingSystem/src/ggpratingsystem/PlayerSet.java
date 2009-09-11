/*
    Copyright (C) 2009 Stephan Schiffel <stephan.schiffel@gmx.de>

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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class PlayerSet {
	
	private Map<String, Player> players = new HashMap<String, Player>();

	public PlayerSet() {
	}

	public Player getPlayer(String playerName) {
		// convert to lower case and remove all non-alphanumeric characters 
		// before querying
		String queryName = playerName.toLowerCase().replaceAll("[^\\p{Alnum}]", "");

		Player result = players.get(queryName);		
		if (result == null) {
			result = new Player(playerName);
			players.put(queryName, result);
		}
		
		return result;
	}

	public Collection<? extends Player> getAllPlayers() {
		return Collections.unmodifiableCollection(players.values());
	}
}

