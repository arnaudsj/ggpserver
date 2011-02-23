/*
    Copyright (C) 2010 Nicolas JEAN <njean42@gmail.com>
                  2010,2011 Stephan Schiffel <stephan.schiffel@gmx.de>

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

import java.util.AbstractCollection;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import tud.auxiliary.CrossProductMap;
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
	
	protected static final Logger logger = Logger.getLogger(StatesTracker.class.getName());
	
	protected GameInterface<TermType, StateType> game;
	protected Collection<StateType> currentPossibleStates;
	protected RoleInterface<TermType> role;

	public StatesTracker(GameInterface<TermType, StateType> game, StateType initialState, RoleInterface<TermType> role) {
		this.game = game;
		this.currentPossibleStates = Collections.singleton(initialState);
		this.role = role;
		logger.info("StatesTracker()");
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
		logger.info(
				"statesUpdate for \"" + role + "\" seeing " + seesTerms
				+ " with " + currentPossibleStates.size() + " currentPossibleStates yields "
				+ nextPossibleStates.size() + " nextPossibleStates");
		if (nextPossibleStates.size()==0) {
			logger.severe("no successor state for states: " + Arrays.toString(currentPossibleStates.toArray()) + ", seesTerms: " + seesTerms);
		}
		currentPossibleStates = nextPossibleStates;
		return Collections.unmodifiableCollection(currentPossibleStates);
	}
	
	public Collection<JointMoveInterface<TermType>> computeJointMoves(StateType state) {
		// compute legal moves for all roles
		HashMap<RoleInterface<TermType>, Collection<? extends MoveInterface<TermType>>> legalMovesMap = new HashMap<RoleInterface<TermType>, Collection<? extends MoveInterface<TermType>>>();
		for(RoleInterface<TermType> role: game.getOrderedRoles()) {
			legalMovesMap.put(role, state.getLegalMoves(role));
		}
		// build the cross product
		final CrossProductMap<RoleInterface<TermType>, MoveInterface<TermType>> jointMovesMap = new CrossProductMap<RoleInterface<TermType>, MoveInterface<TermType>>(legalMovesMap);
		// wrap the elements of the cross product in JointMove<TermType>
		// the following is an on-the-fly collection that just refers to "jointMoves" above 
		Collection<JointMoveInterface<TermType>> jointMoves = new AbstractCollection<JointMoveInterface<TermType>>(){
			@Override
			public Iterator<JointMoveInterface<TermType>> iterator() {
				final Iterator<Map<RoleInterface<TermType>, MoveInterface<TermType>>> iterator = jointMovesMap.iterator();
				return new Iterator<JointMoveInterface<TermType>>(){
					@Override public boolean hasNext() { return iterator.hasNext(); }

					@Override public JointMoveInterface<TermType> next() { return new JointMove<TermType>(game.getOrderedRoles(), iterator.next()); }

					@Override public void remove() { iterator.remove();	}
				};
			}

			@Override
			public int size() {
				return jointMovesMap.size();
			}
		};
		// System.out.println("legal joint moves: " + jointMoves);
		return jointMoves;
	}
	
	private boolean isPossible(StateType state, JointMoveInterface<TermType> jointMove, Collection<TermType> seesTerms) {
		Collection<TermType> shouldSee = state.getSeesTerms(role, jointMove);
		// logger.info(role + " sees "+ shouldSee + " in " + state + " with " + jointMove);
		return shouldSee.equals(seesTerms);
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