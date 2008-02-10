package tud.gamecontroller.players;

public class RemotePlayerInfo extends PlayerInfo {

	private String host;
	private int port;

	public RemotePlayerInfo(String name, String host, int port) {
		this(0, name, host, port);
	}

	public RemotePlayerInfo(int roleindex, String name, String host, int port) {
		super(roleindex, name);
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
