package tud.gamecontroller;

public class PlayerFactory {

	public static Player createRemotePlayer(RemotePlayerInfo info) {
		return new RemotePlayer(info.getHost(), info.getPort());
	}
	public static Player createRandomPlayer(RandomPlayerInfo info) {
		return new RandomPlayer();
	}
	public static Player createLegalPlayer(LegalPlayerInfo info) {
		return new LegalPlayer();
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
