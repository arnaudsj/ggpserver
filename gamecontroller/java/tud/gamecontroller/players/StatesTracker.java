/*
    Copyright (C) 2010 Nicolas JEAN <njean42@gmail.com>
                  2010 Stephan Schiffel <stephan.schiffel@gmx.de>

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
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.impl.JointMove;
import tud.gamecontroller.term.TermInterface;

/**
 * A StatesTracker keeps track of all possible states of a game from the perspective of some player. 
 * @param <TermType>
 * @param <StateType>
 */
public class StatesTracker<TermType extends TermInterface, StateType extends StateInterface<TermType, ? extends StateType>> {
	
	protected GameInterface<TermType, StateType> game;
	protected Collection<StateType> currentPossibleStates;
	protected RoleInterface<TermType> role;

	public StatesTracker(GameInterface<TermType, StateType> game, StateType initialState, RoleInterface<TermType> role) {
		this.game = game;
		this.currentPossibleStates = Collections.singleton(initialState);
		this.role = role;
	}
	
	public Collection<StateType> statesUpdate(Collection<TermType> seesTerms) {
		Set<StateType> nextPossibleStates = new HashSet<StateType>();
		for (StateType state: currentPossibleStates) {
			for (JointMoveInterface<TermType> jointMove: computeJointMoves(state)) {
				if (isPossible(state, jointMove, seesTerms)) {
					StateType newState = state.getSuccessor(jointMove);
					nextPossibleStates.add(newState);
				}
			}
		}
		currentPossibleStates = nextPossibleStates;
		Logger.getLogger(StatesTracker.class.getName()).info(
				"statesUpdate for \"" + role + "\" seeing " + seesTerms
				+ " with " + currentPossibleStates.size() + " currentPossibleStates yields "
				+ currentPossibleStates.size() + " nextPossibleStates");
		return Collections.unmodifiableCollection(currentPossibleStates);
	}
	
	public Collection<JointMoveInterface<TermType>> computeJointMoves(StateType state) {
		int nbJointMoves = 1;
		for(RoleInterface<TermType> role: this.game.getOrderedRoles()) {
			nbJointMoves *= state.getLegalMoves(role).size();
		}
		//System.out.println("\nThere are "+nbJointMoves+" possible JointMoves.");
		Vector<JointMoveInterface<TermType>> legalJointMoves = new Vector<JointMoveInterface<TermType>>(nbJointMoves);
		for (int i = 0; i < nbJointMoves; i++)
			legalJointMoves.add(new JointMove<TermType>( game.getOrderedRoles() ));
		
		for(RoleInterface<TermType> role: game.getOrderedRoles()) {
			//System.out.println("\nFilling JointMoves with move of "+role);
			Collection<? extends MoveInterface<TermType>> legalMoves = state.getLegalMoves(role);
			int nbLm = legalMoves.size();
			int moveIndex=0;
			for (MoveInterface<TermType> move : legalMoves) {
				for (int i = moveIndex * nbJointMoves/nbLm; i < (moveIndex+1) * nbJointMoves/nbLm; i++) {
					//System.out.print(i+"·");
					JointMoveInterface<TermType> currentJointMove = legalJointMoves.get(i);
					currentJointMove.put(role, move);
				}
				moveIndex++;
			}
		}
		//System.out.println("oneStatesTracker.legalJointMoves(...) = "+legalJointMoves);
		return legalJointMoves;
	}
	
	
	private boolean isPossible(StateType state, JointMoveInterface<TermType> jointMove, Collection<TermType> seesTerms) {
		Collection<TermType> shouldSee = state.getSeesTerms(role, jointMove); 
		return shouldSee.equals(seesTerms);
		//System.out.println(shouldSee+".equals( "+seesFluents+" ) = "+b);
	}
	
	/** 
	 * @return moves that are legal in all of the current possible states 
	 */
	public Collection<? extends MoveInterface<TermType>> computeLegalMoves() {
		
		Collection<? extends MoveInterface<TermType>> legalMoves = null;
		for (StateType state: currentPossibleStates) {
			Collection<? extends MoveInterface<TermType>> stateLegalMoves = state.getLegalMoves(role);
			//System.out.println( "stateLegalMoves = "+stateLegalMoves );
			if (legalMoves == null) {
				legalMoves = new HashSet<MoveInterface<TermType>>(stateLegalMoves);
			} else {
				legalMoves.retainAll(stateLegalMoves);
			}
			//System.out.println( "Until now, our legalMoves are = "+legalMoves );
		}
		// System.out.println( "oneStatesTracker.legalMoves() for "+this.role+"("+legalMoves.size()+") = "+legalMoves );
		return legalMoves;
	}
	
	
}