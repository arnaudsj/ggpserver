/*
    Copyright (C) 2008 Stephan Schiffel <stephan.schiffel@gmx.de>

    This file is part of GameController.

    GameController is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GameController is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GameController.  If not, see <http://www.gnu.org/licenses/>.
*/

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
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.impl.Game;
import tud.gamecontroller.game.impl.RunnableMatch;
import tud.gamecontroller.game.impl.State;
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
	
	private final ReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory;

	private GameController<
		TermType,
		ReasonerStateInfoType
		> gameController=null;
	
	public AbstractGameControllerRunner(final ReasonerFactory<TermType, ReasonerStateInfoType> reasonerFactory) {
		super();
		this.reasonerFactory = reasonerFactory;
	}

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
		RunnableMatchInterface<TermType, State<TermType, ReasonerStateInfoType>> match=
				new RunnableMatch<TermType, ReasonerStateInfoType>(
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
		return new Game<TermType, ReasonerStateInfoType>(gameDescription, name, reasonerFactory);
	}

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
