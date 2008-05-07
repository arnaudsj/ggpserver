package tud.gamecontroller.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

import tud.gamecontroller.AbstractGameControllerRunner;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.logging.PlainTextLogFormatter;
import tud.gamecontroller.logging.UnbufferedStreamHandler;
import tud.gamecontroller.players.LegalPlayerInfo;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.gamecontroller.players.RemotePlayerInfo;
import tud.gamecontroller.term.TermInterface;

public abstract class AbstractGameControllerCLIRunner<
		RoleType extends RoleInterface,
		MoveType extends MoveInterface,
		StateType extends StateInterface<RoleType, MoveType, ? extends TermInterface, ? extends StateType>,
		GameType extends GameInterface<? extends RoleType, StateType>
		> extends AbstractGameControllerRunner<
			RoleType, MoveType, StateType, GameType
			>{

	protected void setupLogger(Logger logger) {
		super.setupLogger(logger);
		logger.setUseParentHandlers(false);
		logger.addHandler(new UnbufferedStreamHandler(System.out, new PlainTextLogFormatter()));
		logger.setLevel(Level.ALL);
	}

	private File gameFile=null;
	private int startClock=0, playClock=0;
	private boolean doPrintXML=false;
	private String styleSheet=null;
	private String xmlOutputDir=null;
	private String matchID=null;
	private File scrambleWordList=null;
	private Collection<PlayerInfo> playerInfos=null;
	
	public AbstractGameControllerCLIRunner(){
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
		run();
	}

	private void parseCommandLine(String argv[]){
		int index=0;
		if(argv.length>=4){
			matchID=argv[index]; ++index;
			gameFile=getFileArg(argv[index], "game file", true); ++index;
			startClock=getIntArg(argv[index],"startclock"); ++index;
			playClock=getIntArg(argv[index],"playclock"); ++index;
			
			playerInfos=new LinkedList<PlayerInfo>();
			while(index<argv.length){
				if(argv[index].equals("-printxml")){
					doPrintXML=true; ++index;
					if(index+2<argv.length){
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
			if(argv.length>=index+4){
				int roleindex=getIntArg(argv[index], "roleindex"); ++index;
				if(roleindex<1){
					System.err.println("roleindex out of bounds");
					printUsage();
					System.exit(-1);
				}
				String name=argv[index]; ++index;
				String host=argv[index]; ++index;
				int port=getIntArg(argv[index], "port"); ++index;
				player=new RemotePlayerInfo(roleindex-1, name, host, port);
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
				player=new LegalPlayerInfo(roleindex-1);
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
				player=new RandomPlayerInfo(roleindex-1);
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
		System.out.println("usage:\n java -jar gamecontroller.jar MATCHID GAMEFILE STARTCLOCK PLAYCLOCK [ -printxml OUTPUTDIR XSLT ] [-scramble WORDFILE] { -remote ROLEINDEX NAME HOST PORT | -legal ROLEINDEX | -random ROLEINDEX } ...");
		System.out.println("example:\n java -jar gamecontroller.jar A_Tictactoe_Match tictactoe.gdl 120 30 -remote 2 MyPlayer localhost 4000");
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

	protected GameType createGame(File gameFile){
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
//	protected abstract Match<TermType,StateType,Player<TermType, StateType>> createMatch(String matchID, GameType game, int startClock, int playClock, Map<Role<TermType>, Player<TermType, StateType>> players);

	protected abstract GameType createGame(String gameDescription, String gameName);

	protected boolean doPrintXML() {
		return doPrintXML;
	}

	protected GameType getGame() {
		return createGame(gameFile);
	}

	protected String getMatchID() {
		return matchID;
	}

	protected int getStartClock() {
		return startClock;
	}

	protected int getPlayClock() {
		return playClock;
	}

	protected Collection<PlayerInfo> getPlayerInfos() {
		return playerInfos;
	}

	protected File getScrambleWordListFile() {
		return scrambleWordList;
	}

	protected String getStyleSheet() {
		return styleSheet;
	}

	protected String getXmlOutputDir() {
		return xmlOutputDir;
	}

}
	