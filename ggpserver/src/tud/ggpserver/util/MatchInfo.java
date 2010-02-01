package tud.ggpserver.util;

import java.util.ArrayList;
import java.util.List;

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
