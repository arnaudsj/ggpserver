package tud.gamecontroller.players;

public abstract class PlayerInfo {
	private int roleindex;
	private String name;

	public PlayerInfo(int roleindex, String name) {
		this.roleindex=roleindex;
		this.name=name;
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

}
