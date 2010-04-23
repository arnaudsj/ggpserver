/*
    Copyright (C) 2010 Stephan Schiffel <stephan.schiffel@gmx.de>
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

package tud.gamecontroller.players;

import java.util.ArrayList;
import java.util.List;

import tud.gamecontroller.ConnectionEstablishedNotifier;
import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.auxiliary.ChangeableBoolean;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.term.TermInterface;


public class HumanPlayer<TermType extends TermInterface, StateType extends StateInterface<TermType, ? extends StateType>>
		extends LocalPlayer<TermType, StateType> {
	
	protected List<? extends MoveInterface<TermType>> currentLegalMoves;
	protected boolean quickConfirm;
	protected MoveInterface<TermType> move;
	
	// synchronisers
	protected ChangeableBoolean confirmed;
	protected ChangeableBoolean legalMovesAvailable;
	
	private long messageReceiveTime;
	
	public HumanPlayer(String name) {
		super(name, GDLVersion.v2);
		confirmed = new ChangeableBoolean(false);
		currentLegalMoves = null;
		logger.info("HumanPlayer("+name+")");
		legalMovesAvailable = new ChangeableBoolean(false);
		quickConfirm = false;
		move = null;
	}
	
	public MoveInterface<TermType> getMove() {
		return move;
	}

	protected void waitForConfirmed(long timeout) {
		logger.info("waiting for a confirm from " + name + " ...");
		long endTime = System.currentTimeMillis() + timeout;
		long timeLeft = timeout;
		try {
			synchronized (confirmed) {
				while(!confirmed.isTrue() && timeLeft>0) {
					confirmed.wait(timeLeft);
					timeLeft = endTime - System.currentTimeMillis();
				}
			}
		} catch (InterruptedException e) {
			logger.severe("InterruptedException: "+e);
		}
		if(confirmed.isTrue()) {
			logger.info("move was confirmed by " + name);
		}else{
			logger.info("time ran out for " + name);
		}
	}
	
	@Override
	public void gameStart(RunnableMatchInterface<TermType, StateType> match, RoleInterface<TermType> role, ConnectionEstablishedNotifier notifier) {
		messageReceiveTime = System.currentTimeMillis();
		super.gameStart(match, role, notifier);
		computeLegalMoves();
		waitForConfirmed(messageReceiveTime + match.getStartclock()*1000 - 100 - System.currentTimeMillis());
	}

	@Override
	public MoveInterface<TermType> gamePlay(Object seesTerms, ConnectionEstablishedNotifier notifier) {
		messageReceiveTime = System.currentTimeMillis();
		return super.gamePlay(seesTerms, notifier);
	}

	@Override
	protected MoveInterface<TermType> getNextMove() {
		if(getCurrentStep() != 1) {
			computeLegalMoves();
		}
		waitForConfirmed(messageReceiveTime + match.getPlayclock()*1000 - 100 - System.currentTimeMillis());
		synchronized (legalMovesAvailable) {
			legalMovesAvailable.setFalse();
		}
		return move;
	}

	private void computeLegalMoves() {
		synchronized (legalMovesAvailable) {
			assert(!legalMovesAvailable.isTrue()); // otherwise we could forget an already submitted move
			synchronized (confirmed) {
				confirmed.setFalse();
				if (getGdlVersion() == GDLVersion.v1) { // Regular GDL
					currentLegalMoves = new ArrayList<MoveInterface<TermType>>(currentState.getLegalMoves(role));
				} else { // GDL-II
					currentLegalMoves = new ArrayList<MoveInterface<TermType>>(statesTracker.computeLegalMoves());
				}
				
				logger.info("new currentLegalMoves: "+currentLegalMoves);
				move = currentLegalMoves.iterator().next(); // sets default move for our player, in case he or she doesn't choose one
				if(quickConfirm && currentLegalMoves.size() == 1) {
					logger.info("quick confirming move: " + move);
					confirmed.setTrue();
					confirmed.notifyAll();
				}else{
					legalMovesAvailable.setTrue();
					logger.info("legalMovesAvailable set to True");
				}
			}
		}
	}

	public List<? extends MoveInterface<TermType>> getLegalMoves() {
		synchronized (legalMovesAvailable) {
			if (legalMovesAvailable.isTrue()) {
				return currentLegalMoves;
			} else {
				return null; // error value meaning there are no available moves at the moment
			}
		}
	}
	
	public boolean setMove(MoveInterface<TermType> move, int stepNumber) {
		logger.info("setMove("+move+", "+stepNumber+")");
		if (stepNumber == getCurrentStep()) {
			this.move = move;
			return true;
		} else {
			logger.warning("The server asks us to setMove() for step "+stepNumber+", but we are in step "+getCurrentStep());
			return false; // notifying the action cannot be performed
		}
	}
	
	public boolean confirm(int stepNumber) {
		logger.info("confirm("+stepNumber+") - currentStepNumber="+getCurrentStep());
		synchronized(confirmed) {
			if (stepNumber == getCurrentStep() && !confirmed.isTrue()) {
				confirmed.setTrue();
				confirmed.notifyAll();
				return true;
			} else {
				logger.warning("wrong step or move was already confirmed!");
				return false;
			}
		}
	}

	public boolean hasConfirmed(int stepNumber) {
		return stepNumber == getCurrentStep() && confirmed.isTrue();
	}

	public boolean getQuickConfirm() {
		return quickConfirm;
	}

	public void setQuickConfirm(boolean quickConfirm) {
		this.quickConfirm = quickConfirm;
	}
	
}
