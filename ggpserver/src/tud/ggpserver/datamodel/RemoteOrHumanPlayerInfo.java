package tud.ggpserver.datamodel;


public interface RemoteOrHumanPlayerInfo {
	
	public final String STATUS_ACTIVE = "active";
	public final String STATUS_INACTIVE = "inactive";
	public final String STATUS_NEW = "new";
	
	public String getName(); // any implementer should be extending PlayerInfo and therefore have a getName() method
	
}
