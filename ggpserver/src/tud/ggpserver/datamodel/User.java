package tud.ggpserver.datamodel;


public class User {
	private final String userName;
	
	/**
	 * Use AbstractDBConnector.getUser() instead
	 */
	protected User(String userName) {
		this.userName = userName;
	}
	
	public String getUserName() {
		return userName;
	}

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((userName == null) ? 0 : userName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final User other = (User) obj;
		if (userName == null) {
			if (other.userName != null)
				return false;
		} else if (!userName.equals(other.userName))
			return false;
		return true;
	}
}
