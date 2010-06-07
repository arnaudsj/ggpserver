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

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.impl.JointMove;
import tud.gamecontroller.game.impl.State;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.playerthreads.AbstractPlayerThread;
import tud.gamecontroller.playerthreads.PlayerThreadPlay;
import tud.gamecontroller.playerthreads.PlayerThreadStart;
import tud.gamecontroller.playerthreads.PlayerThreadStop;
import tud.gamecontroller.term.TermInterface;

public class GameController<
		TermType extends TermInterface,
		ReasonerStateInfoType
		>{
	
	/**
	 * defines the minimal delay in milliseconds between receiving the last reply and sending the next play message 
	 */
	private static final int DELAY_BEFORE_NEXT_MESSAGE=500; 
	/**
	 * defines the extra time in milliseconds that is added to the normal start clock and play clock before a player is said to
	 * have timed out  
	 */
	private static final int EXTRA_DEADLINE_TIME=1000; 
	
	private RunnableMatchInterface<TermType, State<TermType, ReasonerStateInfoType>> match;
	private GameInterface<TermType, State<TermType, ReasonerStateInfoType>> game;
	private State<TermType, ReasonerStateInfoType> currentState;
	private int startclock;
	private int playclock;
	private Map<RoleInterface<TermType>, Integer> goalValues=null;
	private Logger logger;
	private Collection<GameControllerListener> listeners;
	
	public GameController(RunnableMatchInterface<TermType, State<TermType, ReasonerStateInfoType>> match) {
		this(match, Logger.getLogger(GameController.class.getName()));
	}

	public GameController(RunnableMatchInterface<TermType, State<TermType, ReasonerStateInfoType>> match, Logger logger) {
		this.match=match;
		this.logger=logger;
		this.game=match.getGame();
		this.startclock=match.getStartclock();
		this.playclock=match.getPlayclock();
		listeners=new LinkedList<GameControllerListener>();
		if(this.game.getGdlVersion() == GDLVersion.v2) {
			logger.info("gdlVersion = II");
		}
	}

	public void addListener(GameControllerListener l){
		listeners.add(l);
	}

	public void removeListener(GameControllerListener l){
		listeners.remove(l);
	}
	
	private void fireGameStart(State<TermType, ReasonerStateInfoType> currentState){
		for(GameControllerListener l:listeners){
			l.gameStarted(match, currentState);
		}
	}
	private void fireGameStep(JointMoveInterface<TermType> jointMove, State<TermType, ReasonerStateInfoType> currentState){
		for(GameControllerListener l:listeners){
			l.gameStep(jointMove, currentState);
		}
	}
	private void fireGameStop(State<TermType, ReasonerStateInfoType> currentState, Map<RoleInterface<TermType>, Integer> goalValues){
		for(GameControllerListener l:listeners){
			l.gameStopped(currentState, goalValues);
		}
	}
	
	public void runGame() throws InterruptedException {
		int step=1;
		currentState=game.getInitialState();
		State<TermType, ReasonerStateInfoType> priorState = currentState;
		JointMoveInterface<TermType> priorJointMove=null;
		logger.info("match:"+match.getMatchID()+", GDL "+this.game.getGdlVersion());
		logger.info("game:"+match.getGame().getName());
		logger.info("starting game with startclock="+startclock+", playclock="+playclock);
		logger.info("step:"+step);
		logger.info("current state:"+currentState);
		fireGameStart(currentState);
		gameStart();
		while(!currentState.isTerminal()){
			Thread.sleep(DELAY_BEFORE_NEXT_MESSAGE);
			JointMoveInterface<TermType> jointMove = gamePlay(step, priorJointMove, priorState);
			priorState=currentState; 
			currentState=currentState.getSuccessor(jointMove);
			fireGameStep(jointMove, currentState);
			priorJointMove=jointMove;
			step++;
			logger.info("step:"+step);
			logger.info("current state:"+currentState);
		}
		
		String goalmsg="Game over! results: ";
		goalValues=new HashMap<RoleInterface<TermType>, Integer>();
		for(RoleInterface<TermType> role:game.getOrderedRoles()){
			int gv=currentState.getGoalValue(role);
			goalValues.put(role,gv);
			goalmsg+=gv+" ";
		}

		fireGameStop(currentState, goalValues);
		gameStop(priorJointMove, priorState);

		String runtimeMsg="runtimes (in ms): ";
		for(RoleInterface<TermType> role:game.getOrderedRoles()){
			long runtime=match.getPlayer(role).getTotalRuntime();
			runtimeMsg+=runtime+" ";
		}
		logger.info(goalmsg);
		logger.info(runtimeMsg);
		logger.info("Done.");
	}

	private void runThreads(Collection<? extends AbstractPlayerThread<?, ?>> threads, Level loglevel) throws InterruptedException{
		try {
			for(AbstractPlayerThread<?, ?> t:threads){
				t.start();
			}
			for(AbstractPlayerThread<?, ?> t:threads){
				if(!t.waitUntilDeadline()){
					String message = "player "+t.getPlayer()+" timed out!";
					GameControllerErrorMessage errorMessage = new GameControllerErrorMessage(GameControllerErrorMessage.TIMEOUT, message, t.getPlayer().getName());
					match.notifyErrorMessage(errorMessage);
					logger.log(loglevel, message, errorMessage);
				}
			}
		} finally {
			// interrupt the threads
			for(AbstractPlayerThread<?, ?> t:threads){
				if (t.isAlive()) t.interrupt();
			}
		}
	}

	private void gameStart() throws InterruptedException {
		Collection<PlayerThreadStart<TermType, State<TermType, ReasonerStateInfoType>>> playerthreads=new LinkedList<PlayerThreadStart<TermType, State<TermType, ReasonerStateInfoType>>>();
		for(RoleInterface<TermType> role:game.getOrderedRoles()){
			logger.info("role: "+role+" => player: "+match.getPlayer(role));
			playerthreads.add(new PlayerThreadStart<TermType, State<TermType, ReasonerStateInfoType>>(role, match.getPlayer(role), match, startclock*1000+EXTRA_DEADLINE_TIME));
		}
		logger.info("Sending start messages ...");
		runThreads(playerthreads, Level.WARNING);
		logger.info("time after gameStart's runThreads: "+new Date(System.currentTimeMillis()));
	}

	private JointMoveInterface<TermType> gamePlay(int step, JointMoveInterface<TermType> priorJointMove, State<TermType, ReasonerStateInfoType> priorState) throws InterruptedException {
		
		JointMoveInterface<TermType> jointMove = new JointMove<TermType>(game.getOrderedRoles());
		Collection<PlayerThreadPlay<TermType, State<TermType, ReasonerStateInfoType>>> playerthreads = new LinkedList<PlayerThreadPlay<TermType, State<TermType, ReasonerStateInfoType>>>();
		
		for(RoleInterface<TermType> role:game.getOrderedRoles()){
			Player<TermType, State<TermType, ReasonerStateInfoType>> player = match.getPlayer(role);
			Object seesTerms = getSeesTermsForRole(role, player, priorState, priorJointMove);
			playerthreads.add(new PlayerThreadPlay<TermType, State<TermType, ReasonerStateInfoType>>(role, player, match, seesTerms, playclock*1000+EXTRA_DEADLINE_TIME));
		}
		
		logger.info("Sending play messages ...");
		runThreads(playerthreads, Level.SEVERE);
		for(PlayerThreadPlay<TermType, State<TermType, ReasonerStateInfoType>> pt:playerthreads){
			RoleInterface<TermType> role=pt.getRole();
			MoveInterface<TermType> move=pt.getMove();
			if(move==null || !currentState.isLegal(role, move)){
				Player<TermType, State<TermType, ReasonerStateInfoType>> player = match.getPlayer(role);
				String message = "Illegal move \""+move+"\" from "+player+ " in step "+step;
				GameControllerErrorMessage errorMessage = new GameControllerErrorMessage(GameControllerErrorMessage.ILLEGAL_MOVE, message, player.getName());
				match.notifyErrorMessage(errorMessage);
				logger.log(Level.SEVERE, message, errorMessage);
				move = currentState.getLegalMove(role);
				if (move == null) {
					message = "no legal move for "+role+" in step "+step+", state: "+currentState.toString();
					errorMessage = new GameControllerErrorMessage(GameControllerErrorMessage.GAME_ERROR, message);
					match.notifyErrorMessage(errorMessage);
					throw new RuntimeException("GameController stopped because: "+message);
				}
				logger.log(Level.SEVERE, message, errorMessage);
				jointMove.put(role,move);
			}else{
				jointMove.put(role,move);
			}
		}
		logger.info("moves: "+jointMove.getKIFForm());
		return jointMove;
	}

	private Object getSeesTermsForRole(RoleInterface<TermType> role,
			Player<TermType, State<TermType, ReasonerStateInfoType>> player,
			State<TermType, ReasonerStateInfoType> priorState,
			JointMoveInterface<TermType> priormoves) {
		/*
		 * Here is the only point at which the difference between regular GDL and GDL-II is made:
		 * - if we play a regular GDL game, we will send the moves as seesTerms;
		 * - and if on the contrary we play a GDL-II game, we will derive the seesTerms from the game description, and send them. 
		 */
		Object seesTerms = null;
		if (priormoves != null) { // not the first play message
			if ( player.getGdlVersion() == GDLVersion.v1) { // GDL-I
				seesTerms = priormoves;
			} else { // GDL-II
				// retrieve seesTerms, and send them in the PLAY/STOP messages
				seesTerms = priorState.getSeesTerms(role, priormoves);
				logger.info("seesTerms("+role+") = " + seesTerms);
			}
		}
		return seesTerms;
	}

	private void gameStop(JointMoveInterface<TermType> priorJointMove, State<TermType, ReasonerStateInfoType> priorState) throws InterruptedException {
		Collection<PlayerThreadStop<TermType, State<TermType, ReasonerStateInfoType>>> playerthreads=new LinkedList<PlayerThreadStop<TermType, State<TermType, ReasonerStateInfoType>>>();
		for(RoleInterface<TermType> role:game.getOrderedRoles()){
			Player<TermType, State<TermType, ReasonerStateInfoType>> player = match.getPlayer(role);
			Object seesTerms = getSeesTermsForRole(role, player, priorState, priorJointMove);
			playerthreads.add(new PlayerThreadStop<TermType, State<TermType, ReasonerStateInfoType>>(role, player, match, seesTerms, playclock*1000+EXTRA_DEADLINE_TIME));
		}
		logger.info("Sending stop messages ...");
		runThreads(playerthreads, Level.WARNING);
	}
	
	public Map<? extends RoleInterface<TermType>, Integer> getGoalValues() {
		return goalValues;
	}

}
