/*
    Copyright (C) 2008 Stephan Schiffel <stephan.schiffel@gmx.de>

    This file is part of GameController.

    GameController is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GameController is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GameController.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.gamecontroller.players;

import tud.gamecontroller.GDLVersion;

public abstract class PlayerInfo {
	
	public static final String TYPE_HUMAN = "human";
	public static final String TYPE_COMPUTER = "computer";
	public static final String TYPE_LEGAL = "legal";
	public static final String TYPE_RANDOM = "random";
	
	private int roleindex;
	private String name;
	
	private GDLVersion gdlVersion;

	public PlayerInfo(int roleindex, String name, GDLVersion gdlVersion) {
		this.roleindex=roleindex;
		this.name=name;
		this.setGdlVersion(gdlVersion);
	}

	public void setRoleindex(int roleindex) {
		this.roleindex = roleindex;
	}

	public int getRoleindex() {
		return roleindex;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
	
	public abstract String getType();

	/**
	 * Two PlayerInfos are considered equal iff they have the same name.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final PlayerInfo other = (PlayerInfo) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	public void setGdlVersion(GDLVersion gdlVersion) {
		this.gdlVersion = gdlVersion;
	}

	public GDLVersion getGdlVersion() {
		return gdlVersion;
	}
}
