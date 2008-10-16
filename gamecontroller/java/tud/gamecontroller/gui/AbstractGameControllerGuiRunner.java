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

package tud.gamecontroller.gui;

import java.io.File;
import java.util.Collection;

import tud.gamecontroller.AbstractGameControllerRunner;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.impl.Game;
import tud.gamecontroller.game.impl.MoveFactory;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.term.TermFactoryInterface;
import tud.gamecontroller.term.TermInterface;

public abstract class AbstractGameControllerGuiRunner<
		TermType extends TermInterface,
		ReasonerStateInfoType
		> extends AbstractGameControllerRunner<TermType, ReasonerStateInfoType> {

	private File gameFile=null;
	private String styleSheet=null;
	private String xmlOutputDir=null;
	private String matchID=null;
	private TermFactoryInterface<TermType> termFactory=null;
	private int startclock=0, playclock=0;
	private File scrambleWordListFile=null;
	private Collection<PlayerInfo> playerInfos=null;
	private GameControllerFrame<TermType,ReasonerStateInfoType> frame=null;
	
	public AbstractGameControllerGuiRunner(File gameFile) {
		super();
		this.gameFile=gameFile;
	}

	@Override
	protected boolean doPrintXML() {
		return xmlOutputDir!=null;
	}

	@Override
	protected Game<TermType, ReasonerStateInfoType> getGame() {
		return createGame(gameFile);
	}

	@Override
	protected String getMatchID() {
		return matchID;
	}

	@Override
	protected MoveFactoryInterface<? extends MoveInterface<TermType>> getMoveFactory() {
		return new MoveFactory<TermType>(termFactory);
	}

	@Override
	protected int getStartClock() {
		return startclock;
	}

	@Override
	protected int getPlayClock() {
		return playclock;
	}

	@Override
	protected Collection<PlayerInfo> getPlayerInfos() {
		return playerInfos;
	}

	@Override
	protected File getScrambleWordListFile() {
		return scrambleWordListFile;
	}

	@Override
	protected String getStyleSheet() {
		return styleSheet;
	}

	@Override
	protected String getXmlOutputDir() {
		return xmlOutputDir;
	}

	public void setGameFile(File gameFile) {
		this.gameFile = gameFile;
	}

	public void setStyleSheet(String styleSheet) {
		this.styleSheet = styleSheet;
	}

	public void setXmlOutputDir(String xmlOutputDir) {
		this.xmlOutputDir = xmlOutputDir;
	}

	public void setMatchID(String matchID) {
		this.matchID = matchID;
	}

	public void setStartclock(int startclock) {
		this.startclock = startclock;
	}

	public void setPlayclock(int playclock) {
		this.playclock = playclock;
	}

	public void setScrambleWordListFile(File scrambleWordListFile) {
		this.scrambleWordListFile = scrambleWordListFile;
	}

	public void setPlayerInfos(Collection<PlayerInfo> playerInfos) {
		this.playerInfos=playerInfos;
	}

	public void runGui() {
		frame=new GameControllerFrame<TermType,ReasonerStateInfoType>(this);
		frame.setVisible(true);
		frame.setupLogger(getLogger());
	}

}
