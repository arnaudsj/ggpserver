package tud.ggpserver.datamodel;


public class RemotePlayerInfo extends tud.gamecontroller.players.RemotePlayerInfo {
	public final static String STATUS_ACTIVE = "active";
	public final static String STATUS_INACTIVE = "inactive";
	public final static String STATUS_NEW = "new";

	private final User owner;
	private final String status;

//	/**
//	 * copy constructor; needed, because role index is unfortunately stored here. destroys the singleton pattern.
//	 */
//	public RemotePlayerInfo(RemotePlayerInfo old) {
//		this(old.getRoleindex(), old.getName(), old.getHost(), old.getPort(), old.getOwner());
//	}
	
	/**
	 * Use AbstractDBConnector.getRemotePlayerInfo() instead
	 */
	protected RemotePlayerInfo(int roleindex, String name, String host, int port, User owner, String status) {
		super(roleindex, name, host, port);
		this.owner = owner;
		this.status = status;
	}

	/**
	 * Use AbstractDBConnector.getRemotePlayerInfo() instead
	 */
	protected RemotePlayerInfo(String name, String host, int port, User owner, String status) {
		super(-1, name, host, port);
		this.owner = owner;
		this.status = status;
	}

	public User getOwner() {
		return owner;
	}


	public String getStatus() {
		return status;
	}

	public static boolean legalStatus(String status) {
		if (status.equals(RemotePlayerInfo.STATUS_NEW)) {
			return true;
		} else if (status.equals(RemotePlayerInfo.STATUS_ACTIVE)) {
			return true;
		} else if (status.equals(RemotePlayerInfo.STATUS_INACTIVE)) {
			return true;
		} else {
			return false;
		}
	}	
}
