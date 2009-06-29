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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class MatchTrace {
	public static final String FLUENT = "fluent";
	public static final String GAME = "game";
	public static final String GAMETRACE = "gametrace";
	public static final String GOAL_VALUE = "goal_value";
	public static final String GOAL_VALUES = "goal_values";
	public static final String LEGAL_MOVES = "legal_moves";	
	public static final String MOVE = "move";
	public static final String MOVES = "moves";
	public static final String ROLE = "role";
	public static final String STATE = "state";
	public static final String STEP = "step";
	public static final String STEPS = "steps";
	public static final String STEP_NUMBER = "nb";
	public static final String TERMINAL = "terminal";
	
	private final String gameName;
	private final List<TracedStep> steps;

	public MatchTrace(String gameName, List<TracedStep> steps) {
		if (gameName == null || steps == null) {
			throw new IllegalArgumentException("gameName and steps must not be null!");
		}
		this.gameName = gameName;
		this.steps = new ArrayList<TracedStep>(steps);
	}
	
	public String getGameName() {
		return gameName;
	}

	public List<TracedStep> getSteps() {
		return steps;
	}
	
	public List<String> getMovesForRole(String role) {
		List<String> moves = new LinkedList<String>();
		for (TracedStep tracedStep : steps) {
			Map<String, String> stepMoves = tracedStep.getMoves();
			if (stepMoves != null) {
				String move = stepMoves.get(role);
				moves.add(move);
			}
			// TODO: error checking
		}
		return moves;
	}
}
