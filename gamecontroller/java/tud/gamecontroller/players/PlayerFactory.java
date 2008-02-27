package tud.gamecontroller.players;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.scrambling.GameScramblerInterface;

public class PlayerFactory {

	public static <
			RoleType extends RoleInterface,
			MoveType> Player<RoleType, MoveType, MatchInterface<?,? extends GameInterface<?, ?>, ?>> createRemotePlayer(RemotePlayerInfo info, MoveFactoryInterface<MoveType> movefactory, GameScramblerInterface gameScrambler) {
		return new RemotePlayer<RoleType, MoveType>(info.getName(), info.getHost(), info.getPort(), movefactory, gameScrambler);
	}
	public static <
			RoleType,
			MoveType> Player<RoleType, MoveType, MatchInterface<RoleType, ? extends GameInterface<?, ? extends StateInterface<RoleType, MoveType, ?, ?>>, ?>> createRandomPlayer(RandomPlayerInfo info) {
		return new RandomPlayer<RoleType, MoveType>(info.getName());
	}
	public static <
			RoleType,
			MoveType> Player<RoleType, MoveType, MatchInterface<RoleType, ? extends GameInterface<?, ? extends StateInterface<RoleType, MoveType, ?, ?>>, ?>> createLegalPlayer(LegalPlayerInfo info) {
		return new LegalPlayer<RoleType, MoveType>(info.getName());
	}
	public static <
		RoleType extends RoleInterface,
		MoveType,
		MatchType extends MatchInterface<RoleType, ? extends GameInterface<?, ? extends StateInterface<RoleType, MoveType, ?, ?>>, ?>
		> Player<? super RoleType, MoveType, ? super MatchType> createPlayer(PlayerInfo info, MoveFactoryInterface<MoveType> termfactory, GameScramblerInterface gameScrambler) {
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
