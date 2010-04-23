/*
    Copyright (C) 2008-2010 Stephan Schiffel <stephan.schiffel@gmx.de>
                  2010 Nicolas JEAN <njean42@gmail.com>

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

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

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
	
	private final ReasonerFactoryInterface<TermType, ReasonerStateInfoType> reasonerFactoryInterface;

	private GameController<
		TermType,
		ReasonerStateInfoType
		> gameController=null;
	
	private GDLVersion gdlVersion = null;
	
	public AbstractGameControllerRunner(final ReasonerFactoryInterface<TermType, ReasonerStateInfoType> reasonerFactory) {
		super();
		this.reasonerFactoryInterface = reasonerFactory;
	}

	public Logger getLogger(){
		return Logger.getLogger(GameController.class.getName());
	}
	
	public void run() throws InterruptedException{
		GameScramblerInterface gameScrambler;
		if(getScrambleWordListFile()!=null){
			gameScrambler=new GameScrambler(getScrambleWordListFile());
		}else{
			gameScrambler=new IdentityGameScrambler();
		}

		Game<TermType, ReasonerStateInfoType> game=getGame();
		Map<RoleInterface<TermType>,Player<TermType, State<TermType, ReasonerStateInfoType>>> players= createPlayers(game, gameScrambler);
		RunnableMatchInterface<TermType, State<TermType, ReasonerStateInfoType>> match=
				new RunnableMatch<TermType, ReasonerStateInfoType>(
						getMatchID(), game, getStartClock(), getPlayClock(), players);
		gameController=new GameController<TermType, ReasonerStateInfoType>(match, getLogger());

		if(doPrintXML()){
			
			if (game.getGdlVersion() == GDLVersion.v1) { // Regular GDL
				
				XMLGameStateWriter gsw = new XMLGameStateWriter(getXmlOutputDir(), getStyleSheet(), null);
				gameController.addListener(gsw);
				
			} else { // GDL-II
				
				for (RoleInterface<TermType> role: game.getOrderedRoles()) {
					XMLGameStateWriter gsw = new XMLGameStateWriter(getXmlOutputDir(), getStyleSheet(), role);
					gameController.addListener(gsw);
				}
				
			}
			
		}
		gameController.runGame();
	}

	private Map<RoleInterface<TermType>,Player<TermType, State<TermType, ReasonerStateInfoType>>> createPlayers(Game<TermType, ReasonerStateInfoType> game, GameScramblerInterface gameScrambler) {
		Map<RoleInterface<TermType>,Player<TermType, State<TermType, ReasonerStateInfoType>>> players=new HashMap<RoleInterface<TermType>,Player<TermType, State<TermType, ReasonerStateInfoType>>>();
		for(PlayerInfo playerInfo:getPlayerInfos()){
			RoleInterface<TermType> role=game.getRole(playerInfo.getRoleindex());
			players.put(role, PlayerFactory. <TermType, State<TermType, ReasonerStateInfoType>> createPlayer(playerInfo, gameScrambler));
		}
		// make sure that we have a player for each role (fill up with random players)
		for(int i=0; i<game.getNumberOfRoles(); i++){
			if(!players.containsKey(game.getRole(i))){
				players.put(game.getRole(i), PlayerFactory. <TermType, State<TermType, ReasonerStateInfoType>> createRandomPlayer(new RandomPlayerInfo(i, game.getGdlVersion())));
			}
		}
		return players;
	}

	protected abstract Collection<PlayerInfo> getPlayerInfos();

	protected abstract Game<TermType, ReasonerStateInfoType> getGame();

	protected abstract File getScrambleWordListFile();

	protected abstract String getStyleSheet();
	
	/**
	 * 
	 * @return the path to the sightFile
	 */
	protected abstract String getSightFile();

	protected abstract String getXmlOutputDir();

	protected abstract boolean doPrintXML();

	protected abstract String getMatchID();

	protected abstract int getPlayClock();

	protected abstract int getStartClock();

	public GDLVersion getGdlVersion() {
		return gdlVersion;
	}

	public void setGdlVersion(GDLVersion gdlVersion) {
		this.gdlVersion = gdlVersion;
	}

	protected Game<TermType, ReasonerStateInfoType> createGame(File gameFile) throws IOException{
		Game<TermType, ReasonerStateInfoType> game = null;
		String sightFile = getSightFile();
		if (sightFile != null) {
			game = new Game<TermType, ReasonerStateInfoType>(gameFile, reasonerFactoryInterface, gdlVersion, getStyleSheet(), new File(getSightFile()));
		} else {
			game = new Game<TermType, ReasonerStateInfoType>(gameFile, reasonerFactoryInterface, gdlVersion, getStyleSheet());
		}
		return game;
	}

	protected Game<TermType, ReasonerStateInfoType> createGame(String gameDescription, String name) throws IOException{
		return new Game<TermType, ReasonerStateInfoType>(gameDescription, name, reasonerFactoryInterface, gdlVersion, getStyleSheet(), getSightFile());
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
