package tud.gamecontroller.players;

import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.TermFactoryInterface;
import tud.gamecontroller.game.TermInterface;
import tud.gamecontroller.scrambling.GameScramblerInterface;

public class PlayerFactory {

	public static <T extends TermInterface, S extends StateInterface<T, S>> Player<T,S> createRemotePlayer(RemotePlayerInfo info, TermFactoryInterface<T> termfactory, GameScramblerInterface gameScrambler) {
		return new RemotePlayer<T,S>(info.getName(), info.getHost(), info.getPort(), termfactory, gameScrambler);
	}
	public static <T extends TermInterface, S extends StateInterface<T, S>> Player<T,S> createRandomPlayer(RandomPlayerInfo info) {
		return new RandomPlayer<T,S>(info.getName());
	}
	public static <T extends TermInterface, S extends StateInterface<T, S>> Player<T,S> createLegalPlayer(LegalPlayerInfo info) {
		return new LegalPlayer<T,S>(info.getName());
	}
	public static <T extends TermInterface, S extends StateInterface<T, S>> Player<T,S> createPlayer(PlayerInfo info, TermFactoryInterface<T> termfactory, GameScramblerInterface gameScrambler) {
		if(info instanceof RemotePlayerInfo){
			return createRemotePlayer((RemotePlayerInfo)info, termfactory, gameScrambler);
		}else if(info instanceof RandomPlayerInfo){
			return createRandomPlayer((RandomPlayerInfo)info);
		}else if(info instanceof LegalPlayerInfo){
			return createLegalPlayer((LegalPlayerInfo)info);
		}
		return null;
	}


}
