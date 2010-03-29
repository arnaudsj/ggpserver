package tud.gamecontroller.players;

import tud.gamecontroller.GDLVersion;

public class HumanPlayerInfo extends PlayerInfo {

	public HumanPlayerInfo(int roleindex, String name) {
		super(roleindex, name, GDLVersion.v2); // a human player can play any game
	}

	@Override
	public String getType() {
		return TYPE_HUMAN;
	}
	
}
