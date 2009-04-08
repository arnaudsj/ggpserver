package tud.ggpserver.datamodel;

import tud.ggpserver.util.HashCodeUtil;

public class User {
	private final String userName;
	
	/**
	 * Use DBConnector.getUser() instead
	 */
	protected User(String userName) {
		this.userName = userName;
	}
	
	public String getUserName() {
		return userName;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof User) {
			User other = (User) obj;
			return other.getUserName().equals(userName);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return HashCodeUtil.hash(HashCodeUtil.SEED, userName);
	}
}
