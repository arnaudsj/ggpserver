package tud.gamecontroller;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import tud.gamecontroller.logging.PlainTextLogFormatter;



public class GameController{
	public static final String GAMEDIR="games/";
	private Game game;
	private State currentState;
	private Player players[];
	private int startclock;
	private int playclock;
	private int goalValues[]=null;
	private Logger logger;
	
	public GameController(Game game, PlayerInfo[] players, int startclock, int playclock) {
		this(game,players,startclock,playclock,null);
	}
	public GameController(Game game, PlayerInfo[] players, int startclock, int playclock, Logger logger) {
		this.game=game;
		this.startclock=startclock;
		this.playclock=playclock;
		this.logger=logger;
		initializePlayers(players);
	}

	private void initializePlayers(PlayerInfo[] definedplayers) {
		players=new Player[game.getNumberOfRoles()];
		goalValues=new int[players.length];
		for(int i=0; i<players.length; i++){
			players[i]=null;
			goalValues[i]=-1;
		}
		for(int i=0; i<definedplayers.length; i++){
			if(definedplayers[i].getRoleindex()<players.length){
				if(players[definedplayers[i].getRoleindex()]==null){
					players[definedplayers[i].getRoleindex()]=PlayerFactory.createPlayer(definedplayers[i]);
				}else{
					throw new IllegalArgumentException("duplicate roleindex in given player list");
				}
			}else{
				throw new IllegalArgumentException("roleindex must be between 0 and n-1 for an n-player game");
			}
		}
		for(int i=0; i<players.length; i++){
			if(players[i]==null){
				players[i]=PlayerFactory.createPlayer(new RandomPlayerInfo(i));
			}
		}
	}

	public void runGame() {
		int step=1;
		currentState=game.getInitialState();
		Move[] priormoves=null;
		log(Level.INFO, "step:"+step+"\n"+"current state:"+currentState);
		gameStart();
		while(!currentState.isTerminal()){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final Move[] moves = gamePlay(step, priormoves);
			currentState=currentState.getSuccessor(moves);
			priormoves=moves;
			step++;
			log(Level.INFO, "step:"+step+"\n"+"current state:"+currentState);
		}
		gameStop(priormoves);
		String goalmsg="Game over! results: ";
		for(int i=0;i<players.length;i++){
			goalValues[i]=currentState.getGoalValue(game.getRole(i+1));
			goalmsg+=goalValues[i]+" ";
		}
		log(Level.INFO, goalmsg+"\n");
	}


	private void gameStart() {
		PlayerThreadStart[] playerthreads=new PlayerThreadStart[players.length];
		for(int i=0;i<players.length;i++){
			log(Level.INFO, "player "+i+": "+players[i]);
			playerthreads[i]=new PlayerThreadStart(players[i], game, game.getRole(i+1), startclock, playclock);
			playerthreads[i].start();
		}
		long startTime=System.currentTimeMillis(), deadline=startTime+startclock*1000+1000;
		for(int i=0;i<players.length;i++){
			if(!waitForThread(playerthreads[i], deadline)){
				log(Level.SEVERE, "player "+players[i]+" timed out!");
			}
		}
	}

	private Move[] gamePlay(int step, Move[] priormoves) {
		final Move[] moves=new Move[players.length];
		PlayerThreadPlay[] playerthreads=new PlayerThreadPlay[players.length];
		for(int i=0;i<players.length;i++){
			playerthreads[i]=new PlayerThreadPlay(players[i], priormoves);
			playerthreads[i].start();
		}
		long startTime=System.currentTimeMillis(), deadline=startTime+playclock*1000+1000;
		for(int i=0;i<players.length;i++){
			if(waitForThread(playerthreads[i], deadline)){
				moves[i]=playerthreads[i].getMove();
			}else{
				log(Level.SEVERE, "player "+players[i]+" timed out!");
				moves[i]=currentState.getLegalMove(game.getRole(i+1));
			}
			playerthreads[i]=null;
			if(moves[i]==null || !currentState.isLegal(game.getRole(i+1), moves[i])){
				log(Level.SEVERE, "Illegal move \""+moves[i]+"\" from "+players[i]+ " in step "+step);
				moves[i]=currentState.getLegalMove(game.getRole(i+1));
			}
		}
		String movesmsg="moves: ";
		for(int i=0;i<moves.length;i++){
			movesmsg+=moves[i]+" ";
		}
		log(Level.INFO, movesmsg);
		return moves;
	}

	private void gameStop(Move[] priormoves) {
		PlayerThreadStop[] playerthreads=new PlayerThreadStop[players.length];
		for(int i=0;i<players.length;i++){
			playerthreads[i]=new PlayerThreadStop(players[i], priormoves);
			playerthreads[i].start();
		}
		long startTime=System.currentTimeMillis(), deadline=startTime+playclock*1000;
		for(int i=0;i<players.length;i++){
			if(!waitForThread(playerthreads[i], deadline)){
				log(Level.WARNING, "player "+players[i]+" timed out! (non critical)");
			}
		}
	}
	
	private boolean waitForThread(Thread t, long deadLineMillis){
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

	private void log(Level level, String msg){
		if(logger!=null){
			logger.log(level, msg);
		}
	}
	
	private class PlayerThreadStart extends Thread {
		private Player player;
		private GameInterface game;
		private int startclock;
		private int playclock;
		private Role role;
		
		public PlayerThreadStart(Player player, GameInterface game, Role role, int startclock, int playclock){
			this.player=player;
			this.game=game;
			this.role=role;
			this.startclock=startclock;
			this.playclock=playclock;
		}
		public void run(){
			player.gameStart(game, role, startclock, playclock);
		}
	}
	private class PlayerThreadPlay extends Thread {
		private Move move;
		private Player player;
		private Move[] priormoves;
		
		public PlayerThreadPlay(Player player, Move[] priormoves){
			this.player=player;
			this.priormoves=priormoves;
			this.move=null;
		}
		public Move getMove() {
			return move;
		}
		public void run(){
			move=player.gamePlay(priormoves);
		}
	}
	private class PlayerThreadStop extends Thread {
		private Player player;
		private Move[] priormoves;
		
		public PlayerThreadStop(Player player, Move[] priormoves){
			this.player=player;
			this.priormoves=priormoves;
		}
		public void run(){
			player.gameStop(priormoves);
		}
	}

	private static PlayerInfo[] parsePlayerArguments(int index, String argv[], Game game){
		PlayerInfo[] playerinfos=new PlayerInfo[game.getNumberOfRoles()];
		for(int i=0;i<playerinfos.length;i++){
			playerinfos[i]=null;
		}
		int roleindex=0;
		String host="";
		int port=0;
		PlayerInfo player=null;
		while(index<argv.length){
			if(argv[index].equals("-remote")){
				if(argv.length>index+4){
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
				}else{
					System.err.println("missing arguments");
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
					System.err.println("missing arguments");
					printUsage();
					System.exit(-1);
				}
				if(argv[index].equals("-legal")){
					player=new LegalPlayerInfo(roleindex-1);
				}else{
					player=new RandomPlayerInfo(roleindex-1);
				}
			}else{
				System.err.println("invalid argument: "+argv[index]);
				printUsage();
				System.exit(-1);
			}
			if(playerinfos[roleindex-1]!=null){
				playerinfos[roleindex-1]=player;
			}else{
				System.err.println("duplicate roleindex: "+roleindex);
				printUsage();
				System.exit(-1);
			}
		}
		return playerinfos;
	}
	
	public static void printUsage(){
		System.out.println("usage:\n java -jar gamecontroller.jar GAMEFILE STARTCLOCK PLAYCLOCK { -remote ROLEINDEX HOST PORT | -legal ROLEINDEX | -random ROLEINDEX } ...");
		System.out.println("e.g.: java -jar gamecontroller.jar tictactoe.gdl 30 5 -remote 2 localhost 4001");
	}
	public static void main(String argv[]){
		Game game=null;
		int startclock=0, playclock=0;
		
		if(argv.length>=3){
			game=Game.readFromFile(argv[0]);
			try{
				startclock=Integer.parseInt(argv[1]);
			}catch(NumberFormatException ex){
				System.err.println("startclock argument is not an integer");
				printUsage();
				System.exit(-1);
			}
			try{
				playclock=Integer.parseInt(argv[2]);
			}catch(NumberFormatException ex){
				System.err.println("playclock argument is not an integer");
				printUsage();
				System.exit(-1);
			}
			PlayerInfo[] playerinfos=parsePlayerArguments(3, argv, game);
			Logger logger=Logger.getAnonymousLogger();
			logger.addHandler(new StreamHandler(System.out, new PlainTextLogFormatter()));
			logger.setLevel(Level.ALL);
			GameController gc=new GameController(game, playerinfos, startclock, playclock, logger);
			System.out.println("game:"+argv[0]);
			gc.runGame();
		}else{
			System.err.println("wrong number of arguments");
			System.exit(-1);
		}
	}

}
