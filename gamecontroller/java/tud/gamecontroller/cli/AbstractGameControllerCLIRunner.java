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

package tud.gamecontroller.cli;

import java.io.File;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import tud.gamecontroller.AbstractGameControllerRunner;
import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.ReasonerFactoryInterface;
import tud.gamecontroller.game.impl.Game;
import tud.gamecontroller.logging.PlainTextLogFormatter;
import tud.gamecontroller.logging.UnbufferedStreamHandler;
import tud.gamecontroller.players.LegalPlayerInfo;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.gamecontroller.players.RemotePlayerInfo;
import tud.gamecontroller.term.TermInterface;


public abstract class AbstractGameControllerCLIRunner<
		TermType extends TermInterface,
		ReasonerStateInfoType> extends AbstractGameControllerRunner<
			TermType, ReasonerStateInfoType
			>{

	private File gameFile=null;
	private int startClock=0, playClock=0;
	private boolean doPrintXML=false;
	private String styleSheet=null;
	private String sightFile=null;
	private String xmlOutputDir=null;
	private String matchID=null;
	private File scrambleWordList=null;
	private Collection<PlayerInfo> playerInfos=null;
	
	public AbstractGameControllerCLIRunner(ReasonerFactoryInterface<TermType, ReasonerStateInfoType> reasonerFactory){
		super(reasonerFactory);
		Logger logger=getLogger();
		logger.setUseParentHandlers(false);
		logger.addHandler(new UnbufferedStreamHandler(System.out, new PlainTextLogFormatter()));
		logger.setLevel(Level.ALL);
	}
	
	public void runFromCommandLine(String argv[]){
		gameFile=null;
		startClock=0; playClock=0;
		doPrintXML=false;
		styleSheet=null;
		xmlOutputDir=null;
		matchID=null;
		scrambleWordList=null;
		playerInfos=null;
		parseCommandLine(argv);
		try {
			run();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void parseCommandLine(String argv[]){
		int index=0;
		if(argv.length>=5){
			matchID=argv[index]; ++index;
			gameFile=getFileArg(argv[index], "game file", true); ++index;
			startClock=getIntArg(argv[index],"startclock"); ++index;
			playClock=getIntArg(argv[index],"playclock"); ++index;
			int gdlVersionInt = getIntArg(argv[index],"gdl version");
			++index;
			if(gdlVersionInt == 1) {
				setGdlVersion(GDLVersion.v1); 
			} else if(gdlVersionInt == 2) {
				setGdlVersion(GDLVersion.v2);
			} else {
				System.err.println("gdl version \""+argv[index]+"\" not recognized");
				printUsage();
				System.exit(-1);
			}
			
			sightFile = null;
			playerInfos=new LinkedList<PlayerInfo>();
			while(index<argv.length){
				if(argv[index].equals("-printxml")){
					doPrintXML=true; ++index;
					if(index+1<argv.length){
						xmlOutputDir=argv[index]; ++index;
						styleSheet=argv[index]; ++index;
					}else{
						missingArgumentsExit(argv[index-1]);
					}
				}else if(argv[index].equals("-scramble")){
					++index;
					if(index<argv.length){
						scrambleWordList=getFileArg(argv[index], "word list", true); ++index;
					}else{
						missingArgumentsExit(argv[index-1]);
					}
				} else if (argv[index].equals("-sightfile")) {
					++index;
					if(index<argv.length){
						sightFile=argv[index]; ++index;
					}else{
						missingArgumentsExit(argv[index-1]);
					}
				}else{
					index=parsePlayerArguments(index, argv);
				}
			}
		}else{
			System.err.println("wrong number of arguments");
			printUsage();
			System.exit(-1);
		}
	}
	
	private int parsePlayerArguments(int index, String argv[]){
		PlayerInfo player=null;
		if(argv[index].equals("-remote")){
			++index;
			if(argv.length>=index+5){
				int roleindex=getIntArg(argv[index], "roleindex"); ++index;
				if(roleindex<1){
					System.err.println("roleindex out of bounds");
					printUsage();
					System.exit(-1);
				}
				String name=argv[index]; ++index;
				String host=argv[index]; ++index;
				int port=getIntArg(argv[index], "port"); ++index;
				int gdl=getIntArg(argv[index], "gdl"); ++index;
				if (gdl == 1) {
					if (getGdlVersion() != GDLVersion.v1) {
						System.err.println("GDL-I players not allowed in GDL-II game");
						printUsage();
						System.exit(-1);
					}
					player=new RemotePlayerInfo(roleindex-1, name, host, port, GDLVersion.v1);
				} else {
					player=new RemotePlayerInfo(roleindex-1, name, host, port, GDLVersion.v2);
				}
			}else{
				missingArgumentsExit(argv[index-1]);
			}
		}else if(argv[index].equals("-legal")){
			++index;
			if(argv.length>=index+1){
				int roleindex=getIntArg(argv[index], "roleindex"); ++index;
				if(roleindex<1){
					System.err.println("roleindex out of bounds");
					printUsage();
					System.exit(-1);
				}
				player=new LegalPlayerInfo(roleindex-1, getGdlVersion()); // use GDL version of the game for the legal player too
			}else{
				missingArgumentsExit(argv[index-1]);
			}
		}else if(argv[index].equals("-random")){
			++index;
			if(argv.length>=index+1){
				int roleindex=getIntArg(argv[index], "roleindex"); ++index;
				if(roleindex<1){
					System.err.println("roleindex out of bounds");
					printUsage();
					System.exit(-1);
				}
				player=new RandomPlayerInfo(roleindex-1, getGdlVersion()); // use GDL version of the game for the random player too
			}else{
				missingArgumentsExit(argv[index-1]);
			}
		}else{
			System.err.println("invalid argument: "+argv[index]);
			printUsage();
			System.exit(-1);
		}
		for(PlayerInfo p:playerInfos){
			if(p.getRoleindex()==player.getRoleindex()){
				System.err.println("duplicate roleindex: "+player.getRoleindex());
				printUsage();
				System.exit(-1);
			}
		}
		playerInfos.add(player);
		return index;
	}

	private void printUsage(){
		System.out.println("usage:\n java -jar gamecontroller.jar MATCHID GAMEFILE STARTCLOCK PLAYCLOCK GDLVERSION [ -printxml OUTPUTDIR XSLT ] [-sightfile SIGHTFILE] [-scramble WORDFILE] { -remote ROLEINDEX NAME HOST PORT GDLVERSION | -legal ROLEINDEX | -random ROLEINDEX } ...");
		System.out.println("example:\n java -jar gamecontroller.jar A_Tictactoe_Match tictactoe.gdl 120 30 1 -remote 2 MyPlayer localhost 4000 1");
	}

	private int getIntArg(String arg, String argName){
		try{
			return Integer.parseInt(arg);
		}catch(NumberFormatException ex){
			System.err.println(argName+" argument is not an integer");
			printUsage();
			System.exit(-1);
		}
		return -1;
	
	}

	private File getFileArg(String arg, String argName, boolean mustExist) {
		File f=new File(arg);
		if(mustExist && !f.exists()){
			System.err.println(argName+" file: \""+arg+"\" doesn't exist!");
			printUsage();
			System.exit(-1);
		}
		return f;
	}
	
	private void missingArgumentsExit(String arg){
		System.err.println("missing arguments for "+arg);
		printUsage();
		System.exit(-1);
	}

//	protected abstract RunnableMatch<TermType,StateType,Player<TermType, StateType>> createMatch(String matchID, GameType game, int startClock, int playClock, Map<Role<TermType>, Player<TermType, StateType>> players);

	@Override
	protected boolean doPrintXML() {
		return doPrintXML;
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
	protected int getStartClock() {
		return startClock;
	}

	@Override
	protected int getPlayClock() {
		return playClock;
	}

	@Override
	protected Collection<PlayerInfo> getPlayerInfos() {
		return playerInfos;
	}

	@Override
	protected File getScrambleWordListFile() {
		return scrambleWordList;
	}

	@Override
	protected String getStyleSheet() {
		return styleSheet;
	}
	
	@Override
	protected String getSightFile() {
		return sightFile;
	}

	@Override
	protected String getXmlOutputDir() {
		return xmlOutputDir;
	}

}
	
