package tud.ggpserver.datamodel;

import tud.ggpserver.util.HashCodeUtil;


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
	 * Use DBConnector.getRemotePlayerInfo() instead
	 */
	protected RemotePlayerInfo(int roleindex, String name, String host, int port, User owner, String status) {
		super(roleindex, name, host, port);
		this.owner = owner;
		this.status = status;
	}

	/**
	 * Use DBConnector.getRemotePlayerInfo() instead
	 */
	protected RemotePlayerInfo(String name, String host, int port, User owner, String status) {
		super(-1, name, host, port);
		this.owner = owner;
		this.status = status;
	}

	public User getOwner() {
		return owner;
	}

	/**
	 * For now, two RemotePlayerInfos are considered equal if they have the same
	 * name. This makes sense, since the name is also used as primary key in the
	 * MySQL database.
	 */
	@Override
	public boolean equals(Object obj) {		
		if (obj instanceof RemotePlayerInfo) {
			RemotePlayerInfo other = (RemotePlayerInfo) obj;
			return other.getName().equals(getName());
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {		 
		 return HashCodeUtil.hash(HashCodeUtil.SEED, getName());	
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
