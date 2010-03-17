/*
    Copyright (C) 2008-2010 Stephan Schiffel <stephan.schiffel@gmx.de>, Nicolas JEAN <njean42@gmail.com>

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
import java.util.Vector;

import tud.gamecontroller.ConnectionEstablishedNotifier;
import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.game.FluentInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.term.TermInterface;

public abstract class LocalPlayer<TermType extends TermInterface, StateType extends StateInterface<TermType, ? extends StateType>>
		extends AbstractPlayer<TermType, StateType> {
	
	// for regular GDL only
	protected StateInterface<TermType, ?> currentState=null;
	
	// MODIFIED: in GDL-II, as we don't have the rule that the first turn is the only one where no moves are sent, we have to manage this with a boolean
	protected boolean firstTurn;
	protected StatesTracker<TermType, StateType> statesTracker; // TODO: what's that ReasonerStateInfoType (here replaced by '?')
		
	
	public LocalPlayer(String name) {
		super(name);
	}
	
	
	
	@SuppressWarnings("unchecked")
	@Override
	public void gameStart(MatchInterface<TermType, StateType> match, RoleInterface<TermType> role, ConnectionEstablishedNotifier notifier) {
		
		super.gameStart(match, role, notifier);
		notifyStartRunning();
		notifier.connectionEstablished();
		
		this.gdlVersion = match.getGame().getGdlVersion();
		
		// Regular GDL
		currentState=match.getGame().getInitialState();
		
		// GDL-II
		this.firstTurn = true; // MODIFIED
		
		// StateType extends StateInterface<TermType, ? extends StateType>
		this.statesTracker = new StatesTracker (
			match.getGame(),
			match.getGame().getInitialState(),
			role );
		
		notifyStopRunning();
		
	}
	
	/*
	 * @param seesFluents is either:
	 * - with regular GDL, a "JointMoveInterface<TermType>" object that is the jointMove previously done by players
	 * - or with GDL-II, a "Collection<? extends FluentInterface<TermType>>" object that really are SeesTerms
	 */
	@SuppressWarnings("unchecked")
	public MoveInterface<TermType> gamePlay(Object seesFluents, ConnectionEstablishedNotifier notifier) {
		
		notifyStartRunning();
		notifier.connectionEstablished();
		
		if(this.firstTurn) { // MODIFIED
			this.firstTurn = false;
		} else { // calculate the successor(s) of current state(s)
			
			if (this.gdlVersion == GDLVersion.v1) { // Regular GDL
				
				JointMoveInterface<TermType> jointMove = (JointMoveInterface<TermType>) seesFluents;
				currentState = currentState.getSuccessor(jointMove);
				
			} else { // GDL-II
				
				try {
					this.statesTracker.statesUpdate( (Collection<FluentInterface<TermType>>) seesFluents );
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			
		}
		MoveInterface<TermType> move = getNextMove();
		
		notifyStopRunning();
		return move;
	}
	
	
	protected Vector<MoveInterface<TermType>> getLegalMoves () {
		
		Vector<MoveInterface<TermType>> legalMoves = null;
		
		if (this.gdlVersion == GDLVersion.v1) { // Regular GDL
			
			legalMoves = new Vector<MoveInterface<TermType>>( currentState.getLegalMoves(role) );
			
		} else { // GDL-II
			
			legalMoves = new Vector<MoveInterface<TermType>>(this.statesTracker.computeLegalMoves());
			
		}
		
		return legalMoves;
		
	}
	
	
	protected abstract MoveInterface<TermType> getNextMove();
	
	
	public String toString(){
		return "local("+getName()+")";
	}
	
}
