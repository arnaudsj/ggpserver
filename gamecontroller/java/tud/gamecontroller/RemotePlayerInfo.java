package tud.gamecontroller;

public class RemotePlayerInfo extends PlayerInfo {

	private String host;
	private int port;

	public RemotePlayerInfo(int roleindex, String host, int port) {
		super(roleindex);
		this.host=host;
		this.port=port;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
}
