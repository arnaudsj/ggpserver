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

package tud.gamecontroller.players;

import java.util.Collection;

import tud.gamecontroller.ConnectionEstablishedNotifier;
import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.term.TermInterface;

public abstract class LocalPlayer<TermType extends TermInterface, StateType extends StateInterface<TermType, ? extends StateType>>
		extends AbstractPlayer<TermType, StateType> {
	
	// for GDL-I
	protected StateInterface<TermType, ?> currentState = null;
	// for GDL-II
	protected StatesTracker<TermType, StateType> statesTracker = null;
	
	private int currentStep;
	
	public LocalPlayer(String name, GDLVersion gdlVersion) {
		super(name, gdlVersion);
	}
	
	@Override
	public void gameStart(RunnableMatchInterface<TermType, StateType> match, RoleInterface<TermType> role, ConnectionEstablishedNotifier notifier) {
		super.gameStart(match, role, notifier);
		notifyStartRunning();
		notifier.connectionEstablished();
		currentStep = 1;
		if( getGdlVersion()!=match.getGame().getGdlVersion() ) {
			logger.warning("GDL versions of player and game do not match!");
			setGdlVersion(match.getGame().getGdlVersion());
		}
		if(getGdlVersion() == GDLVersion.v1) {
			currentState = match.getGame().getInitialState();
		} else {
			statesTracker = new StatesTracker<TermType, StateType>(match.getGame(), match.getGame().getInitialState(), role);
		}
		notifyStopRunning();
	}
	
	/*
	 * @param seesFluents is either:
	 * - with regular GDL, a "JointMoveInterface<TermType>" object that is the jointMove previously done by players
	 * - or with GDL-II, a "Collection<? extends FluentInterface<TermType>>" object that really are SeesTerms
	 */
	@SuppressWarnings("unchecked")
	public MoveInterface<TermType> gamePlay(Object seesTerms, ConnectionEstablishedNotifier notifier) {
		notifyStartRunning();
		notifier.connectionEstablished();
		if(seesTerms != null) {
			currentStep++;
			// calculate the successor(s) of current state(s)
			if (getGdlVersion() == GDLVersion.v1) { // Regular GDL
				JointMoveInterface<TermType> jointMove = (JointMoveInterface<TermType>) seesTerms;
				currentState = currentState.getSuccessor(jointMove);
			} else { // GDL-II
				statesTracker.statesUpdate((Collection<TermType>) seesTerms);
			}
		}
		MoveInterface<TermType> move = getNextMove();
		notifyStopRunning();
		return move;
	}
	
	public Collection<? extends MoveInterface<TermType>> getLegalMoves() {
		Collection<? extends MoveInterface<TermType>> legalMoves = null;
		if (getGdlVersion() == GDLVersion.v1) { // Regular GDL
			legalMoves = currentState.getLegalMoves(role);
		} else { // GDL-II
			legalMoves = statesTracker.computeLegalMoves();
		}
		return legalMoves;
	}
	
	protected abstract MoveInterface<TermType> getNextMove();
	
	protected int getCurrentStep() {
		return currentStep;
	}
	
	public String toString(){
		return "local("+getName()+")";
	}
	
}
