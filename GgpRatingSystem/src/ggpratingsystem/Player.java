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

import ggpratingsystem.ratingsystems.Rating;
import ggpratingsystem.ratingsystems.RatingFactory;
import ggpratingsystem.ratingsystems.RatingSystemType;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class Player implements Comparable<Player> {
	private static final Logger log = Logger.getLogger(Player.class.getName());
    
	static {
		// inherit default level for package ggpratingsystem
		log.setLevel(null);
	}
	
	private final String name;
	private Map<RatingSystemType, Rating> ratings = new HashMap<RatingSystemType, Rating>();
	
	/**
	 * use PlayerSet.getPlayer(name) instead
	 */
	protected Player(String name) {
		super();
		this.name = name;
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

	public int compareTo(Player other) {
		return name.compareTo(other.getName());
	}

}