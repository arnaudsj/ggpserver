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

import java.util.List;
import java.util.Map;

public class TracedStep {
	private final int stepNumber;
	private final List<String> fluents;
	private final Map<String, List<String>> legalMoves;
	private final Map<String, String> moves;
	private final boolean terminal;
	private final Map<String, List<Integer>> goalValues;

	/**
	 * fluents, legalMoves, moves and goalValues are allowed to be null
	 */
	public TracedStep(int stepNumber, List<String> fluents, Map<String, List<String>> legalMoves, Map<String, String> moves, boolean terminal, Map<String, List<Integer>> goalValues) {
		this.fluents = fluents;
		this.legalMoves = legalMoves;
		this.moves = moves;
		this.terminal = terminal;
		this.goalValues = goalValues;
		this.stepNumber = stepNumber;
	}

	/**
	 * may return null
	 */
	public List<String> getFluents() {
		return fluents;
	}

	/**
	 * may return null
	 */
	public Map<String, List<Integer>> getGoalValues() {
		return goalValues;
	}

	/**
	 * may return null
	 */
	public Map<String, List<String>> getLegalMoves() {
		return legalMoves;
	}

	/**
	 * may return null
	 */
	public Map<String, String> getMoves() {
		return moves;
	}

	public int getStepNumber() {
		return stepNumber;
	}
	public boolean isTerminal() {
		return terminal;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TracedStep other = (TracedStep) obj;
		if (this.stepNumber != other.stepNumber) {
			return false;
		}
		if (this.fluents != other.fluents && (this.fluents == null || !this.fluents.equals(other.fluents))) {
			return false;
		}
		if (this.legalMoves != other.legalMoves && (this.legalMoves == null || !this.legalMoves.equals(other.legalMoves))) {
			return false;
		}
		if (this.moves != other.moves && (this.moves == null || !this.moves.equals(other.moves))) {
			return false;
		}
		if (this.terminal != other.terminal) {
			return false;
		}
		if (this.goalValues != other.goalValues && (this.goalValues == null || !this.goalValues.equals(other.goalValues))) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 89 * hash + this.stepNumber;
		hash = 89 * hash + (this.fluents != null ? this.fluents.hashCode() : 0);
		hash = 89 * hash + (this.legalMoves != null ? this.legalMoves.hashCode() : 0);
		hash = 89 * hash + (this.moves != null ? this.moves.hashCode() : 0);
		hash = 89 * hash + (this.terminal ? 1 : 0);
		hash = 89 * hash + (this.goalValues != null ? this.goalValues.hashCode() : 0);
		return hash;
	}
}
