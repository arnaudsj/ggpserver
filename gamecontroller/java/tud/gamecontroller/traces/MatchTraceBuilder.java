/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de>

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


package tud.gamecontroller.traces;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import tud.gamecontroller.GameControllerListener;
import tud.gamecontroller.game.FluentInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.term.TermInterface;

/**
 *
 * @author martin
 */
public class MatchTraceBuilder implements GameControllerListener {
	private String gameName;
	private List<TracedStep> steps = new LinkedList<TracedStep>();
	private int stepNumber = 1;
	private List<? extends RoleInterface<?>> roles;
	private StateInterface<? extends TermInterface, ?> previousState;
	
	public MatchTrace getTrace() {
		return new MatchTrace(gameName, steps);
	}

	public void gameStarted(RunnableMatchInterface<? extends TermInterface, ?> match, 
			StateInterface<? extends TermInterface, ?> currentState) {
		gameName = match.getGame().getName();
		roles = match.getGame().getOrderedRoles();
		previousState = currentState;
	}
	

	public void gameStep(JointMoveInterface<? extends TermInterface> jointmove, 
			StateInterface<? extends TermInterface, ?> currentState) {
		addTracedStep(jointmove, previousState);
		previousState = currentState;
	}

	public void gameStopped(StateInterface<? extends TermInterface, ?> currentState, Map<? extends RoleInterface<?>, Integer> goalValues) {
		addTracedStep(null, previousState);
	}
	
	/**
	 * @param jointmove may be null
	 */
	@SuppressWarnings("unchecked")
	public void addTracedStep(JointMoveInterface<? extends TermInterface> jointmove, 
			StateInterface<? extends TermInterface, ?> state) {
		
		/* fluents */
		List<String> fluents = new LinkedList<String>();
		
		for(FluentInterface<? extends TermInterface> fluent : state.getFluents()) {
			fluents.add(fluent.getTerm().getKIFForm().toLowerCase().toLowerCase());
		}
		
		if (fluents.size() == 0) {
			fluents = null;
		}

		/* terminal */
		boolean terminal = state.isTerminal();
		
		/* legal moves */
		Map<String, List<String>> legalMoves = new HashMap<String, List<String>>();
		
		if (!terminal) {
			for (RoleInterface role : roles) {
				List<String> legalMovesForRole = new LinkedList<String>();
				Collection<? extends MoveInterface<?>> stateLegalMoves = state.getLegalMoves(role);
				for (MoveInterface<?> legalMove : stateLegalMoves) {
					legalMovesForRole.add(legalMove.getKIFForm().toLowerCase());
				}
				if (legalMovesForRole.size() > 0) {
					legalMoves.put(role.getKIFForm().toLowerCase(), legalMovesForRole);
				}
			}
		}
		if (legalMoves.size() == 0) {
			legalMoves = null;
		}
		
		/* moves */
		Map<String, String> moves = new HashMap<String, String>();
		
		if (jointmove != null) {
			List<? extends MoveInterface<? extends TermInterface>> stateMoves = jointmove.getOrderedMoves();

			for (int i = 0; i < stateMoves.size(); i++) {
				moves.put(roles.get(i).getKIFForm().toLowerCase(), stateMoves.get(i).getKIFForm().toLowerCase());
			}
		}
		
		if (moves.size() == 0) {
			moves = null;
		}
		
		/* goal values */
		Map<String, List<Integer>> goalValues = null;
		if (terminal) {
			goalValues = new HashMap<String, List<Integer>>();

			for (RoleInterface role : roles) {
				// TODO multiple distinct goal values or missing goal values cannot be detected this way
				List<Integer> goalValuesForRole = new LinkedList<Integer>();

				int goalValue = state.getGoalValue(role);
				goalValuesForRole.add(goalValue);
				goalValues.put(role.getKIFForm().toLowerCase(), goalValuesForRole);
			}

			if (goalValues.size() == 0) {
				goalValues = null;
			}
		}
		
		/* finally, add new traced step */
		TracedStep step = new TracedStep(stepNumber, fluents, legalMoves, moves, terminal, goalValues);
		steps.add(step);
		stepNumber++;
	}

	/**
	 * Writes out the previousState. This method should be called if the game 
	 * doesn't terminate normally.
	 */
	public void flush() {
		addTracedStep(null, previousState);
	}
}
