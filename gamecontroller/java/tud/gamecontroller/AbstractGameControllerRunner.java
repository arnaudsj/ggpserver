package tud.gamecontroller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.Game;
import tud.gamecontroller.game.impl.Match;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.players.PlayerFactory;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.gamecontroller.scrambling.GameScrambler;
import tud.gamecontroller.scrambling.GameScramblerInterface;
import tud.gamecontroller.scrambling.IdentityGameScrambler;
import tud.gamecontroller.term.TermInterface;

public abstract class AbstractGameControllerRunner<
		TermType extends TermInterface,
		ReasonerStateInfoType> {

	private GameController<
		TermType,
		ReasonerStateInfoType
		> gameController=null;
	
	public Logger getLogger(){
		return Logger.getLogger("tud.gamecontroller");
	}
	
	public void run() throws InterruptedException{
		GameScramblerInterface gameScrambler;
		if(getScrambleWordListFile()!=null){
			gameScrambler=new GameScrambler(getScrambleWordListFile());
		}else{
			gameScrambler=new IdentityGameScrambler();
		}

		Game<TermType, ReasonerStateInfoType> game=getGame();
		Map<RoleInterface<TermType>,Player<TermType>> players=
				createPlayers(game, gameScrambler);
		Match<TermType, ReasonerStateInfoType> match=
				new Match<TermType, ReasonerStateInfoType>(
						getMatchID(), game, getStartClock(), getPlayClock(), players);
		gameController=new GameController<
			TermType,
			ReasonerStateInfoType
			>(match);

		if(doPrintXML()){
			XMLGameStateWriter gsw=new XMLGameStateWriter(getXmlOutputDir(), getStyleSheet());
			gameController.addListener( gsw);
		}
		gameController.runGame();
	}

	private Map<RoleInterface<TermType>,Player<TermType>> createPlayers(Game<TermType, ReasonerStateInfoType> game, GameScramblerInterface gameScrambler) {
		Map<RoleInterface<TermType>,Player<TermType>> players=new HashMap<RoleInterface<TermType>,Player<TermType>>();
		MoveFactoryInterface<? extends MoveInterface<TermType>> moveFactory=getMoveFactory();
		for(PlayerInfo playerInfo:getPlayerInfos()){
			RoleInterface<TermType> role=game.getRole(playerInfo.getRoleindex());
			players.put(role, PlayerFactory. <TermType> createPlayer(playerInfo, moveFactory, gameScrambler));
		}
		// make sure that we have a player for each role (fill up with random players)
		for(int i=0; i<game.getNumberOfRoles(); i++){
			if(!players.containsKey(game.getRole(i))){
				players.put(game.getRole(i), PlayerFactory. <TermType> createRandomPlayer(new RandomPlayerInfo(i)));
			}
		}
		return players;
	}

	protected abstract Collection<PlayerInfo> getPlayerInfos();

	protected abstract MoveFactoryInterface<? extends MoveInterface<TermType>> getMoveFactory();

	protected abstract Game<TermType, ReasonerStateInfoType> getGame();

	protected abstract File getScrambleWordListFile();

	protected abstract String getStyleSheet();

	protected abstract String getXmlOutputDir();

	protected abstract boolean doPrintXML();

	protected abstract String getMatchID();

	protected abstract int getPlayClock();

	protected abstract int getStartClock();

	protected Game<TermType, ReasonerStateInfoType> createGame(File gameFile){
		StringBuffer sb = new StringBuffer();
		try{
		BufferedReader br = new BufferedReader(new FileReader(gameFile));
		String line;

		while((line=br.readLine())!=null){
			line = line.trim();
			sb.append(line + "\n"); // artificial EOLN marker
		}
		} catch (IOException e){
			System.out.println(e);
			System.exit(-1);
		}
		return createGame(sb.toString(), gameFile.getName());
	}

	protected Game<TermType, ReasonerStateInfoType> createGame(String gameDescription, String name){
		return new Game<TermType, ReasonerStateInfoType>(gameDescription, name, getReasoner(gameDescription, name));
	}

	protected abstract ReasonerInterface<TermType, ReasonerStateInfoType> getReasoner(String gameDescription, String gameName);

	public GameController<
		TermType,
		ReasonerStateInfoType
	> getGameController(){
		return gameController;
	}
	
	public void cleanup() {
		gameController=null;
	}

}
