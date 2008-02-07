package tud.gamecontroller;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.TermFactoryInterface;
import tud.gamecontroller.game.TermInterface;
import tud.gamecontroller.game.javaprover.Game;
import tud.gamecontroller.game.javaprover.State;
import tud.gamecontroller.game.javaprover.Term;
import tud.gamecontroller.game.javaprover.TermFactory;
import tud.gamecontroller.logging.PlainTextLogFormatter;
import tud.gamecontroller.logging.UnbufferedStreamHandler;
import tud.gamecontroller.players.LegalPlayerInfo;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.players.PlayerFactory;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.gamecontroller.players.RemotePlayerInfo;
import tud.gamecontroller.scrambling.GameScrambler;
import tud.gamecontroller.scrambling.GameScramblerInterface;

public class GameController<
		T extends TermInterface,
		S extends StateInterface<T,S>
		>{
	public static final String GAMEDIR="games/";
	private Match<T,S> match;
	private GameInterface<T,S> game;
	private S currentState;
	private List<Player<T,S>> players;
	private int startclock;
	private int playclock;
	private int goalValues[]=null;
	private Logger logger;
	private Collection<GameControllerListener<T,S>> listeners;
	private TermFactoryInterface<T> termfactory;
	
	public GameController(Match<T,S> match, Collection<PlayerInfo> players, TermFactoryInterface<T> termfactory) {
		this(match,players,termfactory,null);
	}
	public GameController(Match<T,S> match, Collection<PlayerInfo> players, TermFactoryInterface<T> termfactory, Logger logger) {
		this.match=match;
		this.game=match.getGame();
		this.startclock=match.getStartclock();
		this.playclock=match.getPlayclock();
		if(logger==null){
			this.logger=Logger.getLogger("tud.gamecontroller");
		}else{
			this.logger=logger;
		}
		this.termfactory=termfactory;
		initializePlayers(players);
		listeners=new LinkedList<GameControllerListener<T,S>>();
	}

	public void addListener(GameControllerListener<T,S> l){
		listeners.add(l);
	}

	public void removeListener(GameControllerListener<T,S> l){
		listeners.remove(l);
	}
	
	private void fireGameStart(S currentState){
		for(GameControllerListener<T,S> l:listeners){
			l.gameStarted(match, players, currentState);
		}
	}
	private void fireGameStep(List<Move<T>> moves, S currentState){
		for(GameControllerListener<T,S> l:listeners){
			l.gameStep(moves, currentState);
		}
	}
	private void fireGameStop(S currentState, int goalValues[]){
		for(GameControllerListener<T,S> l:listeners){
			l.gameStopped(currentState, goalValues);
		}
	}
	
	private void initializePlayers(Collection<PlayerInfo> definedplayers) {
		players=new ArrayList<Player<T,S>>();
		goalValues=new int[game.getNumberOfRoles()];
		for(int i=0; i<game.getNumberOfRoles(); i++){
			players.add(null);
			goalValues[i]=-1;
		}
		for(PlayerInfo p:definedplayers){
			if(p!=null){
				if(p.getRoleindex()<players.size()){
					if(players.get(p.getRoleindex())==null){
						players.set(p.getRoleindex(),PlayerFactory. <T,S> createPlayer(p, termfactory));
					}else{
						throw new IllegalArgumentException("duplicate roleindex in given player list");
					}
				}else{
					throw new IllegalArgumentException("roleindex must be between 0 and n-1 for an n-player game");
				}
			}
		}
		for(int i=0; i<players.size(); i++){
			if(players.get(i)==null){
				players.set(i,PlayerFactory. <T,S> createPlayer(new RandomPlayerInfo(i), termfactory));
			}
		}
	}

	public void runGame() {
		int step=1;
		currentState=game.getInitialState();
		List<Move<T>> priormoves=null;
		logger.info("starting game with startclock="+startclock+", playclock="+playclock);
		logger.info("step:"+step);
		logger.info("current state:"+currentState);
		gameStart();
		fireGameStart(currentState);
		while(!currentState.isTerminal()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			final List<Move<T>> moves = gamePlay(step, priormoves);
			currentState=currentState.getSuccessor(moves);
			fireGameStep(moves, currentState);
			priormoves=moves;
			step++;
			logger.info("step:"+step);
			logger.info("current state:"+currentState);
		}
		gameStop(priormoves);
		String goalmsg="Game over! results: ";
		for(int i=0;i<goalValues.length;i++){
			goalValues[i]=currentState.getGoalValue(game.getRole(i+1));
			goalmsg+=goalValues[i]+" ";
		}
		fireGameStop(currentState, goalValues);
		logger.info(goalmsg);
	}

	private void runThreads(Collection<? extends AbstractPlayerThread<T,S>> threads, Level loglevel){
		for(AbstractPlayerThread<T,S> t:threads){
			t.start();
		}
		for(AbstractPlayerThread<T,S> t:threads){
			if(!t.waitUntilDeadline()){
				logger.log(loglevel, "player "+t.getPlayer()+" timed out!");
			}
		}
	}

	private void gameStart() {
		Collection<PlayerThreadStart<T,S>> playerthreads=new LinkedList<PlayerThreadStart<T,S>>();
		for(int i=0;i<players.size();i++){
			logger.info("player "+i+": "+players.get(i));
			playerthreads.add(new PlayerThreadStart<T,S>(i+1, players.get(i), match, startclock*1000+1000));
		}
		logger.info("Sending start messages ...");
		runThreads(playerthreads, Level.WARNING);
	}

	private List<Move<T>> gamePlay(int step, List<Move<T>> priormoves) {
		final List<Move<T>> moves=new ArrayList<Move<T>>();
		Collection<PlayerThreadPlay<T,S>> playerthreads=new LinkedList<PlayerThreadPlay<T,S>>();
		for(int i=0;i<players.size();i++){
			moves.add(null);
			playerthreads.add(new PlayerThreadPlay<T,S>(i+1, players.get(i), match, priormoves, playclock*1000+1000));
		}
		logger.info("Sending play messages ...");
		runThreads(playerthreads, Level.SEVERE);
		for(PlayerThreadPlay<T,S> pt:playerthreads){
			int i=pt.getRoleIndex()-1;
			Move<T> move=pt.getMove();
			if(move==null || !currentState.isLegal(game.getRole(i+1), move)){
				logger.severe("Illegal move \""+move+"\" from "+players.get(i)+ " in step "+step);
				moves.set(i,currentState.getLegalMove(game.getRole(i+1)));
			}else{
				moves.set(i,move);
			}
		}
		String movesmsg="moves: ";
		for(Move<T> m:moves){
			movesmsg+=m+" ";
		}
		logger.info(movesmsg);
		return moves;
	}

	private void gameStop(List<Move<T>> priormoves) {
		Collection<PlayerThreadStop<T,S>> playerthreads=new LinkedList<PlayerThreadStop<T,S>>();
		for(int i=0;i<players.size();i++){
			playerthreads.add(new PlayerThreadStop<T,S>(i+1, players.get(i), match, priormoves, playclock*1000+1000));
		}
		logger.info("Sending stop messages ...");
		runThreads(playerthreads, Level.WARNING);
	}
	
	public int[] getGoalValues() {
		return goalValues;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}
	private static int parsePlayerArguments(int index, String argv[], Game game, List<PlayerInfo> playerinfos){
		int roleindex=0;
		String host="";
		int port=0;
		PlayerInfo player=null;
		if(index<argv.length){
			if(argv[index].equals("-remote")){
				if(argv.length>index+3){
					roleindex=getIntArg(argv[index+1], "roleindex");
					if(roleindex<1 || roleindex>game.getNumberOfRoles()){
						System.err.println("roleindex out of bounds");
						printUsage();
						System.exit(-1);
					}
					host=argv[index+2];
					port=getIntArg(argv[index+3], "port");
					player=new RemotePlayerInfo(roleindex-1, host, port);
					index+=4;
				}else{
					missingArgumentsExit(argv[index]);
				}
			}else if(argv[index].equals("-legal") || argv[index].equals("-random")){
				if(argv.length>index+1){
					roleindex=getIntArg(argv[index+1], "roleindex");
					if(roleindex<1 || roleindex>game.getNumberOfRoles()){
						System.err.println("roleindex out of bounds");
						printUsage();
						System.exit(-1);
					}
				}else{
					missingArgumentsExit(argv[index]);
				}
				if(argv[index].equals("-legal")){
					player=new LegalPlayerInfo(roleindex-1);
				}else{
					player=new RandomPlayerInfo(roleindex-1);
				}
				index+=2;
			}else{
				System.err.println("invalid argument: "+argv[index]);
				printUsage();
				System.exit(-1);
			}
			if(playerinfos.get(roleindex-1)==null){
				playerinfos.set(roleindex-1,player);
			}else{
				System.err.println("duplicate roleindex: "+roleindex);
				printUsage();
				System.exit(-1);
			}
		}
		return index;
	}

	public static void printUsage(){
		System.out.println("usage:\n java -jar gamecontroller.jar MATCHID GAMEFILE STARTCLOCK PLAYCLOCK [ -printxml OUTPUTDIR XSLT ] [-scramble WORDFILE] { -remote ROLEINDEX HOST PORT | -legal ROLEINDEX | -random ROLEINDEX } ...");
		System.out.println("usage:\n java -jar gamecontroller.jar MATCHID GAMEFILE STARTCLOCK PLAYCLOCK [ -printxml OUTPUTDIR XSLT ] [-scramble WORDFILE] { -remote ROLEINDEX HOST PORT | -legal ROLEINDEX | -random ROLEINDEX } ...");
	}
	public static int getIntArg(String arg, String argName){
		try{
			return Integer.parseInt(arg);
		}catch(NumberFormatException ex){
			System.err.println(argName+" argument is not an integer");
			printUsage();
			System.exit(-1);
		}
		return -1;
	
	}
	public static void missingArgumentsExit(String arg){
		System.err.println("missing arguments for "+arg);
		printUsage();
		System.exit(-1);
	}
	public static void main(String argv[]){
		Game game=null;
		int startclock=0, playclock=0;
		String matchID, stylesheet=null, xmloutputdir=null;
		int index=0;
		GameScramblerInterface gameScrambler=null;
		if(argv.length>=4){
			matchID=argv[index]; ++index;
			game=Game.readFromFile(argv[index]); ++index;
			startclock=getIntArg(argv[index],"startclock"); ++index;
			playclock=getIntArg(argv[index],"playclock"); ++index;
			
			List<PlayerInfo> playerinfos=new LinkedList<PlayerInfo>();
			for(int i=0;i<game.getNumberOfRoles();i++){
				playerinfos.add(null);
			}
			while(index<argv.length){
				if(argv[index].equals("-printxml")){
					if(index+2<argv.length){
						xmloutputdir=argv[index+1];
						stylesheet=argv[index+2];
						index+=3;
					}else{
						missingArgumentsExit(argv[index]);
					}
				}else if(argv[index].equals("-scramble")){
					if(index+1<argv.length){
						gameScrambler=new GameScrambler(new File(argv[index+1]));
						index+=2;
					}else{
						missingArgumentsExit(argv[index]);
					}
				}else{
					index=parsePlayerArguments(index, argv, game, playerinfos);
				}
			}

			Logger logger=Logger.getLogger("tud.gamecontroller");
			logger.setUseParentHandlers(false);
			logger.addHandler(new UnbufferedStreamHandler(System.out, new PlainTextLogFormatter()));
			logger.setLevel(Level.ALL);
			GameController<Term, State> gc=new GameController<Term, State>(new Match<Term, State>(matchID, game, startclock, playclock, gameScrambler), playerinfos, new TermFactory(), logger);
			logger.info("match:"+matchID);
			logger.info("game:"+argv[1]);
			if(stylesheet!=null){
				XMLGameStateWriter<Term, State> gsw=new XMLGameStateWriter<Term, State>(xmloutputdir, stylesheet);
				gc.addListener(gsw);
			}
			gc.runGame();
		}else{
			System.err.println("wrong number of arguments");
			printUsage();
			System.exit(-1);
		}
	}
}
