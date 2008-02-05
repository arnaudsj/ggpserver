package tud.gamecontroller.players;

public class PlayerFactory {

	public static Player createRemotePlayer(RemotePlayerInfo info) {
		return new RemotePlayer(info.getName(), info.getHost(), info.getPort());
	}
	public static Player createRandomPlayer(RandomPlayerInfo info) {
		return new RandomPlayer(info.getName());
	}
	public static Player createLegalPlayer(LegalPlayerInfo info) {
		return new LegalPlayer(info.getName());
	}
	public static Player createPlayer(PlayerInfo info) {
		if(info instanceof RemotePlayerInfo){
			return createRemotePlayer((RemotePlayerInfo)info);
		}else if(info instanceof RandomPlayerInfo){
			return createRandomPlayer((RandomPlayerInfo)info);
		}else if(info instanceof LegalPlayerInfo){
			return createLegalPlayer((LegalPlayerInfo)info);
		}
		return null;
	}


}
