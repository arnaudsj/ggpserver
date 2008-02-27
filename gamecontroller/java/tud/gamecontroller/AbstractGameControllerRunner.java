package tud.gamecontroller;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.players.PlayerFactory;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.gamecontroller.scrambling.GameScrambler;
import tud.gamecontroller.scrambling.GameScramblerInterface;
import tud.gamecontroller.scrambling.IdentityGameScrambler;
import tud.gamecontroller.term.TermInterface;

public abstract class AbstractGameControllerRunner<
		RoleType extends RoleInterface,
		MoveType extends MoveInterface,
		StateType extends StateInterface<RoleType, MoveType, ? extends TermInterface, ? extends StateType>,
		GameType extends GameInterface<? extends RoleType, StateType>
//		,
//		PlayerType extends Player<RoleType, MoveType, ? super MatchInterface<RoleType, GameType, PlayerType>>,
//		MatchType extends MatchInterface<RoleType, GameType, Player<? super RoleType, MoveType, ? super MatchType>>
//		MatchType extends MatchInterface<RoleType, GameType, PlayerType>
//		ListenerType extends GameControllerListener<?,?,? super MatchType,? super StateInterface<RoleType, MoveType, ?, ?>>

//		RoleType extends RoleType,
//		MoveType extends MoveInterface,
//		StateType extends StateInterface<RoleType, MoveType, ?, ?>,
//		GameType extends GameInterface<? extends RoleType, StateType>,
//		PlayerType extends Player<RoleType, MoveType, ? super MatchInterface<RoleType, GameType, PlayerType>>,
//		MatchType extends MatchInterface<RoleType, GameType, PlayerType>


		> {

	private GameController<
//	RoleType,
//	MoveType extends MoveInterface,
//	StateType extends StateInterface<RoleType, MoveType, ?, ?>,
//	GameType extends GameInterface<? extends RoleType, StateType>,
//	MatchType extends MatchInterface<RoleType, GameType, Player<RoleType, MoveType, MatchType>>,
//	ListenerType extends GameControllerListener<?,?,? super MatchType,? super StateInterface<RoleType, MoveType, ?, ?>>

	//	RoleType,
//	MoveType extends MoveInterface,
//	StateType extends StateInterface<RoleType, MoveType, ?, ?>,
//	GameType extends GameInterface<? extends RoleType, StateType>,
//	MatchType extends MatchInterface<
//			RoleType,
//			GameType,
//			Player<? super RoleType, MoveType, ? super MatchType>
//	>,
//	ListenerType extends GameControllerListener<?,?,? super MatchType,? super StateInterface<RoleType, MoveType, ?, ?>>

		RoleType,
		MoveType,
		StateType,
		GameType,
		MatchInterface<RoleType, GameType, Player<? super RoleType, MoveType, ? super MatchInterface<RoleType, GameType, ?>>>,
		GameControllerListener<?,?,? super MatchInterface<RoleType, GameType, Player<? super RoleType, MoveType, ? super MatchInterface<RoleType, GameType, ?>>>,? super StateType>
		> gameController=null;
	
	public void run(){
		GameScramblerInterface gameScrambler;
		if(getScrambleWordListFile()!=null){
			gameScrambler=new GameScrambler(getScrambleWordListFile());
		}else{
			gameScrambler=new IdentityGameScrambler();
		}

		Logger logger=Logger.getLogger("tud.gamecontroller");
		setupLogger(logger);

		GameType game=getGame();
		Map<RoleType,Player<? super RoleType, MoveType, ? super MatchInterface<RoleType, GameType, ?>>> players=
				createPlayers(game, gameScrambler);
		MatchInterface<RoleType, GameType, Player<? super RoleType, MoveType, ? super MatchInterface<RoleType, GameType, ?>>> match=
				new Match<RoleType, GameType, Player<? super RoleType, MoveType, ? super MatchInterface<RoleType, GameType, ?>>>(
						getMatchID(), game, getStartClock(), getPlayClock(), players);
		gameController=new GameController<
		RoleType,
			MoveType,
			StateType,
			GameType,
			MatchInterface<RoleType, GameType, Player<? super RoleType, MoveType, ? super MatchInterface<RoleType, GameType, ?>>>,
			GameControllerListener<?,?,? super MatchInterface<RoleType, GameType, Player<? super RoleType, MoveType, ? super MatchInterface<RoleType, GameType, ?>>>,? super StateType>
			>(match);

		if(doPrintXML()){
			XMLGameStateWriter gsw=new XMLGameStateWriter(getXmlOutputDir(), getStyleSheet());
			//(GameControllerListener<
//				? super RoleType,
//				? super MoveType,
//				? super MatchInterface<
//					RoleType,
//					GameType,
//					Player<RoleType, MoveType, MatchType>
//				>,
//				? super StateInterface<? extends RoleType, ? extends MoveType, ?, ?>>)
			//GameControllerListener<
//ok			RoleType,
//ok			MoveInterface,
//				MatchInterface<
//					?,
//					? extends GameInterface<? extends RoleType,?>,
//					? extends NamedObject
//				>,
//				StateInterface<?,?,? extends TermInterface,?>>
			gameController.addListener( gsw);
		}
		gameController.runGame();
	}

	protected void setupLogger(Logger logger) {}

	private Map<RoleType,Player<? super RoleType, MoveType, ? super MatchInterface<RoleType, GameType, ?>>> createPlayers(GameType game, GameScramblerInterface gameScrambler) {
		Map<RoleType,Player<? super RoleType, MoveType, ? super MatchInterface<RoleType, GameType, ?>>> players=new HashMap<RoleType,Player<? super RoleType, MoveType, ? super MatchInterface<RoleType, GameType, ?>>>();
		MoveFactoryInterface<MoveType> moveFactory=getMoveFactory();
		for(PlayerInfo playerInfo:getPlayerInfos()){
			RoleType role=game.getRole(playerInfo.getRoleindex());
			players.put(role, PlayerFactory. <RoleType,MoveType,MatchInterface<RoleType, GameType, ?>> createPlayer(playerInfo, moveFactory, gameScrambler));
		}
		// make sure that we have a player for each role (fill up with random players)
		for(int i=0; i<game.getNumberOfRoles(); i++){
			if(!players.containsKey(game.getRole(i))){
				players.put(game.getRole(i), PlayerFactory. <RoleType,MoveType> createRandomPlayer(new RandomPlayerInfo(i)));
			}
		}
		return players;
	}

	protected abstract Collection<PlayerInfo> getPlayerInfos();

	protected abstract MoveFactoryInterface<MoveType> getMoveFactory();

	protected abstract GameType getGame();

	protected abstract File getScrambleWordListFile();

	protected abstract String getStyleSheet();

	protected abstract String getXmlOutputDir();

	protected abstract boolean doPrintXML();

	protected abstract String getMatchID();

	protected abstract int getPlayClock();

	protected abstract int getStartClock();

	public GameController<
	RoleType,
	MoveType,
	StateType,
	GameType,
	MatchInterface<RoleType, GameType, Player<? super RoleType, MoveType, ? super MatchInterface<RoleType, GameType, ?>>>,
	GameControllerListener<?,?,? super MatchInterface<RoleType, GameType, Player<? super RoleType, MoveType, ? super MatchInterface<RoleType, GameType, ?>>>,? super StateType>
	> getGameController(){
		return gameController;
	}
}
