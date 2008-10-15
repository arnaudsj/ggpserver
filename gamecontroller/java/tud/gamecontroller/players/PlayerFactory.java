package tud.gamecontroller.players;

import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.scrambling.GameScramblerInterface;
import tud.gamecontroller.term.TermInterface;

public class PlayerFactory {

	public static <TermType extends TermInterface> Player<TermType> createRemotePlayer(RemotePlayerInfo info, MoveFactoryInterface<? extends MoveInterface<TermType>> movefactory, GameScramblerInterface gameScrambler) {
		return new RemotePlayer<TermType>(info.getName(), info.getHost(), info.getPort(), movefactory, gameScrambler);
	}
	public static <TermType extends TermInterface> Player<TermType> createRandomPlayer(RandomPlayerInfo info) {
		return new RandomPlayer<TermType>(info.getName());
	}
	public static <TermType extends TermInterface> Player<TermType> createLegalPlayer(LegalPlayerInfo info) {
		return new LegalPlayer<TermType>(info.getName());
	}
	public static <TermType extends TermInterface> Player<TermType> createPlayer(PlayerInfo info, MoveFactoryInterface<? extends MoveInterface<TermType>> termfactory, GameScramblerInterface gameScrambler) {
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
