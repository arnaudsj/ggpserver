package tud.gamecontroller;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.JointMove;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.playerthreads.AbstractPlayerThread;
import tud.gamecontroller.playerthreads.PlayerThreadPlay;
import tud.gamecontroller.playerthreads.PlayerThreadStart;
import tud.gamecontroller.playerthreads.PlayerThreadStop;

public class GameController<
		RoleType,
		MoveType extends MoveInterface,
		StateType extends StateInterface<? super RoleType, MoveType, ?, ? extends StateType>,
		GameType extends GameInterface<? extends RoleType, ? extends StateType>,
		MatchType extends MatchInterface<
				RoleType,
				GameType,
				Player<? super RoleType, MoveType, ? super MatchInterface<RoleType, GameType, ?>>
		>,
		ListenerType extends GameControllerListener<?,?,? super MatchType,? super StateType>
		>{
	
	/**
	 * defines the minimal delay in milliseconds between receiving the last reply and sending the next play message 
	 */
	private static final int DELAY_BEFORE_NEXT_MESSAGE=100; 
	/**
	 * defines the extra time in milliseconds that is added to the normal start clock and play clock before a player is said to
	 * have timed out  
	 */
	private static final int EXTRA_DEADLINE_TIME=1000; 
	
	private MatchType match;
	private GameInterface<? extends RoleType, ? extends StateType> game;
	private StateType currentState;
	private int startclock;
	private int playclock;
	private Map<RoleType, Integer> goalValues=null;
	private Logger logger;
	private Collection<ListenerType> listeners;

	public GameController(MatchType match) {
		this.match=match;
		this.game=match.getGame();
		this.startclock=match.getStartclock();
		this.playclock=match.getPlayclock();
		listeners=new LinkedList<ListenerType>();
		this.logger=Logger.getLogger("tud.gamecontroller");
	}

	public void addListener(ListenerType l){
		listeners.add(l);
	}

	public void removeListener(ListenerType l){
		listeners.remove(l);
	}
	
	private void fireGameStart(StateType currentState){
		for(ListenerType l:listeners){
			l.gameStarted(match, currentState);
		}
	}
	private void fireGameStep(JointMoveInterface<? extends RoleType, ? extends MoveType> jointMove, StateType currentState){
		for(ListenerType l:listeners){
			l.gameStep(jointMove, currentState);
		}
	}
	private void fireGameStop(StateType currentState, Map<? extends RoleType, Integer> goalValues){
		for(ListenerType l:listeners){
			l.gameStopped(currentState, goalValues);
		}
	}
	
	public void runGame() {
		int step=1;
		currentState=game.getInitialState();
		JointMoveInterface<? extends RoleType, ? extends MoveType> priorJointMove=null;
		logger.info("match:"+match.getMatchID());
		logger.info("game:"+match.getGame().getName());
		logger.info("starting game with startclock="+startclock+", playclock="+playclock);
		logger.info("step:"+step);
		logger.info("current state:"+currentState);
		fireGameStart(currentState);
		gameStart();
		while(!currentState.isTerminal()){
			try {
				Thread.sleep(DELAY_BEFORE_NEXT_MESSAGE);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			JointMoveInterface<? extends RoleType, ? extends MoveType> jointMove = gamePlay(step, priorJointMove);
			currentState=currentState.getSuccessor(jointMove);
			fireGameStep(jointMove, currentState);
			priorJointMove=jointMove;
			step++;
			logger.info("step:"+step);
			logger.info("current state:"+currentState);
		}
		String goalmsg="Game over! results: ";
		goalValues=new HashMap<RoleType, Integer>();
		for(RoleType role:game.getOrderedRoles()){
			int gv=currentState.getGoalValue(role);
			goalValues.put(role,gv);
			goalmsg+=gv+" ";
		}
		logger.info(goalmsg);
		fireGameStop(currentState, goalValues);
		gameStop(priorJointMove);
	}

	private void runThreads(Collection<? extends AbstractPlayerThread<?, ?, ?>> threads, Level loglevel){
		for(AbstractPlayerThread<?, ?, ?> t:threads){
			t.start();
		}
		for(AbstractPlayerThread<?, ?, ?> t:threads){
			if(!t.waitUntilDeadline()){
				logger.log(loglevel, "player "+t.getPlayer()+" timed out!");
			}
		}
	}

	private void gameStart() {
		Collection<PlayerThreadStart<RoleType, MatchType>> playerthreads=new LinkedList<PlayerThreadStart<RoleType, MatchType>>();
		for(RoleType role:game.getOrderedRoles()){
			logger.info("role: "+role+" => player: "+match.getPlayer(role));
			playerthreads.add(new PlayerThreadStart<RoleType, MatchType>(role, match.getPlayer(role), match, startclock*1000+EXTRA_DEADLINE_TIME));
		}
		logger.info("Sending start messages ...");
		runThreads(playerthreads, Level.WARNING);
	}

	private JointMoveInterface<? extends RoleType, ? extends MoveType> gamePlay(int step, JointMoveInterface<? extends RoleType, ? extends MoveType> priormoves) {
		JointMoveInterface<RoleType, MoveType> jointMove=new JointMove<RoleType,MoveType>(game.getOrderedRoles());
		Collection<PlayerThreadPlay<RoleType, MoveType>> playerthreads=new LinkedList<PlayerThreadPlay<RoleType, MoveType>>();
		for(RoleType role:game.getOrderedRoles()){
			playerthreads.add(new PlayerThreadPlay<RoleType, MoveType>(role, match.getPlayer(role), match, priormoves, playclock*1000+EXTRA_DEADLINE_TIME));
		}
		logger.info("Sending play messages ...");
		runThreads(playerthreads, Level.SEVERE);
		for(PlayerThreadPlay<RoleType, MoveType> pt:playerthreads){
			RoleType role=pt.getRole();
			MoveType move=pt.getMove();
			if(move==null || !currentState.isLegal(role, move)){
				logger.severe("Illegal move \""+move+"\" from "+match.getPlayer(role)+ " in step "+step);
				jointMove.put(role,currentState.getLegalMove(role));
			}else{
				jointMove.put(role,move);
			}
		}
		logger.info("moves: "+jointMove.getKIFForm());
		return jointMove;
	}

	private void gameStop(JointMoveInterface<? extends RoleType, ? extends MoveType> priorJointMove) {
		Collection<PlayerThreadStop<RoleType, MoveType>> playerthreads=new LinkedList<PlayerThreadStop<RoleType, MoveType>>();
		for(RoleType role:game.getOrderedRoles()){
			playerthreads.add(new PlayerThreadStop<RoleType, MoveType>(role, match.getPlayer(role), match, priorJointMove, playclock*1000+EXTRA_DEADLINE_TIME));
		}
		logger.info("Sending stop messages ...");
		runThreads(playerthreads, Level.WARNING);
		logger.info("Done.");
	}
	
	public Map<? extends RoleType, Integer> getGoalValues() {
		return goalValues;
	}

}
