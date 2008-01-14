package tud.gamecontroller;

public abstract class PlayerInfo {
	private int roleindex;
	public PlayerInfo(int roleindex) {
		this.roleindex=roleindex;
	}

	public int getRoleindex() {
		return roleindex;
	}

	public void setRoleindex(int roleindex) {
		this.roleindex = roleindex;
	}

}
