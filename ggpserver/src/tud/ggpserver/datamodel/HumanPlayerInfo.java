package tud.ggpserver.datamodel;


public class HumanPlayerInfo extends tud.gamecontroller.players.HumanPlayerInfo implements RemoteOrHumanPlayerInfo {
	
	public HumanPlayerInfo(int roleindex, String name) {
		super(roleindex, name);
	}
	
}
