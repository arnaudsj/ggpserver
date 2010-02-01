package tud.ggpserver.util;

public class PlayerInfo {
	private String playerName;
	private Integer roleindex;
	private Integer goal_value;
	
	public PlayerInfo(String playerName, Integer roleindex, Integer goalValue) {
		super();
		this.playerName = playerName;
		this.roleindex = roleindex;
		goal_value = goalValue;
	}

	public String getPlayerName() {
		return playerName;
	}

	public Integer getRoleindex() {
		return roleindex;
	}

	public Integer getGoalValue() {
		return goal_value;
	}
	
	
}
