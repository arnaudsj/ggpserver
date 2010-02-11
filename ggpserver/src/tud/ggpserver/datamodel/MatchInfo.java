/*
    Copyright (C) 2010 Peter Steinke <peter.steinke@inf.tu-dresden.de>

    This file is part of GGP Server.

    GGP Server is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GGP Server is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GGP Server.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.ggpserver.datamodel;

import java.util.ArrayList;
import java.util.List;

import tud.ggpserver.util.PlayerInfo;

public class MatchInfo {
	private String matchID;
	private List<PlayerInfo> players;
	private String gameName;
	private Integer start_clock;
	private Integer play_clock;
	private String start_time;
	private String status;
	private Integer numberOfRoles = 0;
	
	public MatchInfo(String matchID, String gameName, Integer startClock,
			Integer playClock, String startTime, String status) {
		super();
		this.matchID = matchID;
		this.gameName = gameName;
		start_clock = startClock;
		play_clock = playClock;
		start_time = startTime;
		this.status = status;
		players = new ArrayList<PlayerInfo>();
	}
	
	public void addPlayer(PlayerInfo player) {
		players.add(player);
	}
	
	public Integer getNumberOfRoles() {
		if (numberOfRoles == 0) {
			numberOfRoles = players.size();
		}
		return numberOfRoles;
	}
	
	public void addPlayer(String playerName, Integer roleindex, Integer goalValue) {
		players.add(new PlayerInfo(playerName, roleindex, goalValue));
	}

	public String getMatchID() {
		return matchID;
	}

	public List<PlayerInfo> getPlayers() {
		return players;
	}

	public String getGameName() {
		return gameName;
	}

	public Integer getStartclock() {
		return start_clock;
	}

	public Integer getPlayclock() {
		return play_clock;
	}

	public String getStartTime() {
		return start_time;
	}

	public String getStatus() {
		return status;
	}
	
	
	
}
