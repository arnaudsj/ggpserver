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

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.impl.JointMove;
import tud.gamecontroller.game.impl.State;
import tud.gamecontroller.logging.ErrorMessageListener;
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
		this.match=match;
		this.game=match.getGame();
		this.startclock=match.getStartclock();
		this.playclock=match.getPlayclock();
		listeners=new LinkedList<GameControllerListener>();
		this.logger=Logger.getLogger("tud.gamecontroller");
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
		JointMoveInterface<TermType> priorJointMove=null;
		logger.info("match:"+match.getMatchID());
		logger.info("game:"+match.getGame().getName());
		logger.info("starting game with startclock="+startclock+", playclock="+playclock);
		logger.info("step:"+step);
		logger.info("current state:"+currentState);
		fireGameStart(currentState);
		gameStart();
		while(!currentState.isTerminal()){
			Thread.sleep(DELAY_BEFORE_NEXT_MESSAGE);
			JointMoveInterface<TermType> jointMove = gamePlay(step, priorJointMove);
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
		gameStop(priorJointMove);

		String runtimeMsg="runtimes (in ms): ";
		for(RoleInterface<TermType> role:game.getOrderedRoles()){
			long runtime=match.getPlayer(role).getTotalRuntime();
			runtimeMsg+=runtime+" ";
		}
		logger.info(goalmsg);
		logger.info(runtimeMsg);
		logger.info("Done.");
	}

	private void runThreads(Collection<? extends AbstractPlayerThread<?>> threads, Level loglevel) throws InterruptedException{
		for(AbstractPlayerThread<?> t:threads){
			t.start();
		}
		for(AbstractPlayerThread<?> t:threads){
			if(!t.waitUntilDeadline()){
				String message = "player "+t.getPlayer()+" timed out!";
				GameControllerErrorMessage errorMessage = new GameControllerErrorMessage(GameControllerErrorMessage.TIMEOUT, message, t.getPlayer().getName());
				if (match instanceof ErrorMessageListener) {
					((ErrorMessageListener) match).notifyErrorMessage(errorMessage);
				}
				logger.log(loglevel, message, errorMessage);
			}
		}
	}

	private void gameStart() throws InterruptedException {
		Collection<PlayerThreadStart<TermType>> playerthreads=new LinkedList<PlayerThreadStart<TermType>>();
		for(RoleInterface<TermType> role:game.getOrderedRoles()){
			logger.info("role: "+role+" => player: "+match.getPlayer(role));
			playerthreads.add(new PlayerThreadStart<TermType>(role, match.getPlayer(role), match, startclock*1000+EXTRA_DEADLINE_TIME));
		}
		logger.info("Sending start messages ...");
		runThreads(playerthreads, Level.WARNING);
	}

	private JointMoveInterface<TermType> gamePlay(int step, JointMoveInterface<TermType> priormoves) throws InterruptedException {
		JointMoveInterface<TermType> jointMove=new JointMove<TermType>(game.getOrderedRoles());
		Collection<PlayerThreadPlay<TermType>> playerthreads=new LinkedList<PlayerThreadPlay<TermType>>();
		for(RoleInterface<TermType> role:game.getOrderedRoles()){
			playerthreads.add(new PlayerThreadPlay<TermType>(role, match.getPlayer(role), match, priormoves, playclock*1000+EXTRA_DEADLINE_TIME));
		}
		logger.info("Sending play messages ...");
		runThreads(playerthreads, Level.SEVERE);
		for(PlayerThreadPlay<TermType> pt:playerthreads){
			RoleInterface<TermType> role=pt.getRole();
			MoveInterface<TermType> move=pt.getMove();
			if(move==null || !currentState.isLegal(role, move)){
				Player<TermType> player = match.getPlayer(role);
				String message = "Illegal move \""+move+"\" from "+player+ " in step "+step;
				GameControllerErrorMessage errorMessage = new GameControllerErrorMessage(GameControllerErrorMessage.ILLEGAL_MOVE, message, player.getName());
				if (match instanceof ErrorMessageListener) {
					((ErrorMessageListener) match).notifyErrorMessage(errorMessage);
				}
				logger.log(Level.SEVERE, message, errorMessage);
				jointMove.put(role,currentState.getLegalMove(role));
			}else{
				jointMove.put(role,move);
			}
		}
		logger.info("moves: "+jointMove.getKIFForm());
		return jointMove;
	}

	private void gameStop(JointMoveInterface<TermType> priorJointMove) throws InterruptedException {
		Collection<PlayerThreadStop<TermType>> playerthreads=new LinkedList<PlayerThreadStop<TermType>>();
		for(RoleInterface<TermType> role:game.getOrderedRoles()){
			playerthreads.add(new PlayerThreadStop<TermType>(role, match.getPlayer(role), match, priorJointMove, playclock*1000+EXTRA_DEADLINE_TIME));
		}
		logger.info("Sending stop messages ...");
		runThreads(playerthreads, Level.WARNING);
	}
	
	public Map<? extends RoleInterface<TermType>, Integer> getGoalValues() {
		return goalValues;
	}

}
