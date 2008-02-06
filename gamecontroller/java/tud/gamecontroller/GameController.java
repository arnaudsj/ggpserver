package tud.gamecontroller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

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
import tud.gamecontroller.players.LegalPlayerInfo;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.players.PlayerFactory;
import tud.gamecontroller.players.PlayerInfo;
import tud.gamecontroller.players.RandomPlayerInfo;
import tud.gamecontroller.players.RemotePlayerInfo;

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
		logger.info("starting game with startclock="+startclock+", playerclock="+playclock);
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

	private void runThreads(Collection<? extends AbstractPlayerThread<T,S>> threads, long timeout, Level loglevel){
		for(AbstractPlayerThread<T,S> t:threads){
			t.start();
			t.setDeadLine(System.currentTimeMillis()+timeout);
		}
		for(AbstractPlayerThread<T,S> t:threads){
			if(!waitForThread(t, t.getDeadLine())){
				logger.log(loglevel, "player "+t.getPlayer()+" timed out!");
			}
		}
	}

	private void gameStart() {
		Collection<PlayerThreadStart<T,S>> playerthreads=new LinkedList<PlayerThreadStart<T,S>>();
		for(int i=0;i<players.size();i++){
			logger.info("player "+i+": "+players.get(i));
			playerthreads.add(new PlayerThreadStart<T,S>(i+1, players.get(i), match));
		}
		runThreads(playerthreads, startclock*1000+1000, Level.WARNING);
	}

	private List<Move<T>> gamePlay(int step, List<Move<T>> priormoves) {
		final List<Move<T>> moves=new ArrayList<Move<T>>();
		Collection<PlayerThreadPlay<T,S>> playerthreads=new LinkedList<PlayerThreadPlay<T,S>>();
		for(int i=0;i<players.size();i++){
			moves.add(null);
			playerthreads.add(new PlayerThreadPlay<T,S>(i+1, players.get(i), match, priormoves));
		}
		runThreads(playerthreads, playclock*1000+1000, Level.SEVERE);
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
			playerthreads.add(new PlayerThreadStop<T,S>(i+1, players.get(i), match, priormoves));
		}
		runThreads(playerthreads, playclock*1000+1000, Level.WARNING);
	}
	
	private static boolean waitForThread(Thread t, long deadLineMillis){
		long timeLeft=deadLineMillis-System.currentTimeMillis();
		if(timeLeft<=0){
			timeLeft=1;
		}
		if(t.isAlive()){
			try {
				t.join(timeLeft);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(t.isAlive()){
			t.interrupt();
			return false;
		}else{
			return true;
		}
	}

	public int[] getGoalValues() {
		return goalValues;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	private static Collection<PlayerInfo> parsePlayerArguments(int index, String argv[], Game game){
		List<PlayerInfo> playerinfos=new LinkedList<PlayerInfo>();
		for(int i=0;i<game.getNumberOfRoles();i++){
			playerinfos.add(null);
		}
		int roleindex=0;
		String host="";
		int port=0;
		PlayerInfo player=null;
		while(index<argv.length){
			if(argv[index].equals("-remote")){
				if(argv.length>index+3){
					try{
						roleindex=Integer.parseInt(argv[index+1]);
					}catch(NumberFormatException ex){
						System.err.println("roleindex argument is not an integer");
						printUsage();
						System.exit(-1);
					}
					if(roleindex<1 || roleindex>game.getNumberOfRoles()){
						System.err.println("roleindex out of bounds");
						printUsage();
						System.exit(-1);
					}
					host=argv[index+2];
					try{
						port=Integer.parseInt(argv[index+3]);
					}catch(NumberFormatException ex){
						System.err.println("port argument is not an integer");
						printUsage();
						System.exit(-1);
					}
					player=new RemotePlayerInfo(roleindex-1, host, port);
					index+=4;
				}else{
					System.err.println("missing arguments for -remote");
					printUsage();
					System.exit(-1);
				}
			}else if(argv[index].equals("-legal") || argv[index].equals("-random")){
				if(argv.length>index+1){
					try{
						roleindex=Integer.parseInt(argv[index+1]);
					}catch(NumberFormatException ex){
						System.err.println("roleindex argument is not an integer");
						printUsage();
						System.exit(-1);
					}
					if(roleindex<1 || roleindex>game.getNumberOfRoles()){
						System.err.println("roleindex out of bounds");
						printUsage();
						System.exit(-1);
					}
				}else{
					System.err.println("missing arguments for "+argv[index]);
					printUsage();
					System.exit(-1);
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
		return playerinfos;
	}
	
	public static void printUsage(){
		System.out.println("usage:\n java -jar gamecontroller.jar MATCHID GAMEFILE STARTCLOCK PLAYCLOCK [ -printxml OUTPUTDIR XSLT ] { -remote ROLEINDEX NAME HOST PORT | -legal ROLEINDEX | -random ROLEINDEX } ...");
		System.out.println("e.g.: java -jar gamecontroller.jar A_TicTacToe_Match tictactoe.gdl 30 5 -remote 2 localhost 4001");
	}
	public static void main(String argv[]){
		Game game=null;
		int startclock=0, playclock=0;
		String matchID, stylesheet=null, xmloutputdir=null;
		if(argv.length>=3){
			matchID=argv[0];
			game=Game.readFromFile(argv[1]);
			try{
				startclock=Integer.parseInt(argv[2]);
			}catch(NumberFormatException ex){
				System.err.println("startclock argument is not an integer");
				printUsage();
				System.exit(-1);
			}
			try{
				playclock=Integer.parseInt(argv[3]);
			}catch(NumberFormatException ex){
				System.err.println("playclock argument is not an integer");
				printUsage();
				System.exit(-1);
			}
			Collection<PlayerInfo> playerinfos;
			if(argv[4].equals("-printxml")){
				xmloutputdir=argv[5];
				stylesheet=argv[6];
				playerinfos=parsePlayerArguments(7, argv, game);
			}else{
				playerinfos=parsePlayerArguments(4, argv, game);
			}
			Logger logger=Logger.getLogger("tud.gamecontroller");
			logger.setUseParentHandlers(false);
			logger.addHandler(new StreamHandler(System.out, new PlainTextLogFormatter()));
			logger.setLevel(Level.ALL);
			GameController<Term, State> gc=new GameController<Term, State>(new Match<Term, State>(matchID, game, startclock, playclock), playerinfos, new TermFactory(), logger);
			logger.info("match:"+matchID);
			logger.info("game:"+argv[1]);
			if(stylesheet!=null){
				XMLGameStateWriter<Term, State> gsw=new XMLGameStateWriter<Term, State>(xmloutputdir, stylesheet);
				gc.addListener(gsw);
			}
			gc.runGame();
		}else{
			System.err.println("wrong number of arguments");
			System.exit(-1);
		}
	}
}
