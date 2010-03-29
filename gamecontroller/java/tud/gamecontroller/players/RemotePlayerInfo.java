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

public class RemotePlayerInfo extends PlayerInfo {

	private String host;
	private int port;

	public RemotePlayerInfo(String name, String host, int port, GDLVersion gdlVersion) {
		this(0, name, host, port, gdlVersion);
	}

	public RemotePlayerInfo(int roleindex, String name, String host, int port, GDLVersion gdlVersion) {
		super(roleindex, name, gdlVersion);
		this.host=host;
		this.port=port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public String getType() {
		return TYPE_COMPUTER;
	}
}
