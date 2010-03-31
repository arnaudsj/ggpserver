/*
    Copyright (C) 2010 Stephan Schiffel <stephan.schiffel@gmx.de>, Nicolas JEAN <njean42@gmail.com>

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

package tud.gamecontroller.players;

import java.util.Collection;
import java.util.List;
import java.util.LinkedList;
import java.util.logging.Logger;

import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.auxiliary.ChangeableBoolean;
import tud.gamecontroller.auxiliary.ChangeableInt;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.playerthreads.MoveMemory;
import tud.gamecontroller.term.TermInterface;


public class HumanPlayer<TermType extends TermInterface, StateType extends StateInterface<TermType, ? extends StateType>>
		extends LocalPlayer<TermType, StateType> {
	
	protected static final Logger logger = Logger.getLogger(HumanPlayer.class.getName());
	
	protected MoveMemory<TermType> moveMemory;
	protected int currentStepNumber;
	protected List<? extends MoveInterface<TermType>> currentLegalMoves;
	protected boolean quickConfirm;
	
	// synchronisers
	protected ChangeableBoolean ready;
	protected ChangeableInt confirm; // should always contain the stepNumber of the game step that should now be confirmed (i.e., HumanPlayer confirmed the last one, or the game went further)
	protected ChangeableBoolean legalMovesAvailable;
	
	
	public HumanPlayer(String name) {
		super(name, GDLVersion.v2);
		ready = new ChangeableBoolean(false);
		confirm = new ChangeableInt(0);
		currentLegalMoves = null;
		logger.info("HumanPlayer("+name+")");
		currentStepNumber = 0;
		legalMovesAvailable = new ChangeableBoolean(false);
		quickConfirm = false;
	}
	
	@Override
	protected void waitForReady() throws InterruptedException {
		logger.info("waiting for "+name+" to be ready...");
		synchronized (ready) {
			while (! ready.isTrue())
				ready.wait();
		}
		logger.info(name+" is ready!");
	}
	
	public void setReady () {
		synchronized (ready) {
			ready.setTrue();
			ready.notify();
		}
	}
	
	public boolean isReady () {
		if (ready == null) return false;
		return ready.isTrue();
	}
	
	@Override
	protected MoveInterface<TermType> getNextMove() {
		logger.info("HumanPlayer.getNextMove(), waiting for a confirm...");
		
		try {
			synchronized (confirm) {
				while (confirm.getValue() <= currentStepNumber) { // as long as we haven't confirm our move (confirm.value > currentStepNumber)
					confirm.wait();
				}
			}
			logger.info("HumanPlayer.getNextMove(), was notified (confirm)");
		} catch (InterruptedException e) {
			//e.printStackTrace();
			logger.info("getNextMove() was interrupted, a move has been anyway selected");
		}
		
		return this.moveMemory.getMove();
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public MoveInterface<TermType> gamePlay(Object seesTerms, MoveMemory<TermType> moveMemory) {
		
		logger.info("HumanPlayer.gamePlay(···)");
		
		notifyStartRunning();
		moveMemory.connectionEstablished();
		
		if(this.firstTurn) {
			this.firstTurn = false;
		} else {
			// calculate the successor(s) of current state(s)
			if (getGdlVersion() == GDLVersion.v1) { // Regular GDL
				JointMoveInterface<TermType> jointMove = (JointMoveInterface<TermType>) seesTerms;
				currentState = currentState.getSuccessor(jointMove);
			} else { // GDL-II
				statesTracker.statesUpdate((Collection<TermType>) seesTerms);
			}
			
		}
		
		synchronized (legalMovesAvailable) {
			synchronized (confirm) {
				this.moveMemory = moveMemory;
				this.currentStepNumber++;
				confirm.setValue(currentStepNumber);
				if (getGdlVersion() == GDLVersion.v1) { // Regular GDL
					currentLegalMoves = new LinkedList(currentState.getLegalMoves(role));
				} else { // GDL-II
					currentLegalMoves = new LinkedList(statesTracker.computeLegalMoves());
				}
				
				logger.info("new currentLegalMoves: "+currentLegalMoves);
				moveMemory.setMove(currentLegalMoves.iterator().next()); // sets default move for our player, in case he or she doesn't choose one
				this.legalMovesAvailable.setTrue();
				logger.info("legalMovesAvailable set to True");
			}
		}
		
		MoveInterface<TermType> move = getNextMove();
		
		synchronized (legalMovesAvailable) {
			legalMovesAvailable.setFalse();
		}
		
		notifyStopRunning();
		return move;
		
	}
	
	public List<? extends MoveInterface<TermType>> getLegalMoves () {
		
		synchronized (legalMovesAvailable) {
			if (legalMovesAvailable.isTrue()) {
				return this.currentLegalMoves;
			} else {
				return null; // error value meaning there are no available moves at the moment
			}
		}
	}
	
	public boolean setMove (MoveInterface<TermType> move, int stepNumber) {
		logger.info("HumanPayer, setMove ("+move+", "+stepNumber+"))");
		if (stepNumber == confirm.getValue()) {
			moveMemory.setMove(move);
			return true;
		} else {
			logger.info("The server asks us to setMove() for step "+stepNumber+", but we should now set for step "+confirm.getValue());
			return false; // notifying the action cannot be performed
		}
	}
	
	public MoveInterface<TermType> getMove () {
		if (moveMemory == null) return null;
		return moveMemory.getMove();
	}
	
	public boolean confirm (int stepNumber) {
		logger.info("confirm("+stepNumber+") - currentStepNumber="+currentStepNumber+"; confirm.getValue()="+confirm.getValue());
		if (stepNumber == confirm.getValue()) {
			synchronized(confirm) {
				confirm.setValue(stepNumber+1);
				confirm.notify();
			}
			return true;
		} else {
			logger.info("The server asks us to confirm() for step "+stepNumber+", but it has already been done.");
			return false;
		}
		
	}

	public boolean hasConfirmed (int stepNumber) {
		return confirm.getValue() > stepNumber;
	}

	public void toggleQuickConfirm() {
		quickConfirm = !quickConfirm;
	}

	public boolean getQuickConfirm() {
		return quickConfirm;
	}
	
}
