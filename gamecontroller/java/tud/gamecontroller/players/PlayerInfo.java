package tud.gamecontroller.players;

public abstract class PlayerInfo {
	private int roleindex;
	private String name;

	public PlayerInfo(int roleindex, String name) {
		this.roleindex=roleindex;
		this.name=name;
	}

	public int getRoleindex() {
		return roleindex;
	}

	public String getName() {
		return name;
	}

}
