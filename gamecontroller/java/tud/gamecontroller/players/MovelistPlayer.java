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
package tud.gamecontroller.players;

import java.util.Collection;
import java.util.List;

import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.exceptions.NoLegalMoveException;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.term.TermInterface;

/**
 * This class implements a player that simply replays a fixed moves list. Since
 * it doesn't examine the state in doing so, it is only useful if the sequence
 * of states is known in advance, i.e., for retracing a match.
 * 
 * @author martin
 */
public class MovelistPlayer<TermType extends TermInterface,
		StateType extends StateInterface<TermType, ? extends StateType>>
		extends LocalPlayer<TermType, StateType> {

	private final List<String> moveStrings;
	private int stepNumber = 0;
	
	public MovelistPlayer(String name, List<String> moveStrings) {
		super(name, GDLVersion.v1);
		this.moveStrings = moveStrings;
	}

	@Override
	protected MoveInterface<TermType> getNextMove() {
		if (stepNumber >= moveStrings.size()) {
			throw new IllegalStateException("match is longer than number of recorded moves!");
		}
		String moveString = moveStrings.get(stepNumber);
		stepNumber++;
		
		Collection<? extends MoveInterface<TermType>> legalMoves = currentState.getLegalMoves(role);
		for (MoveInterface<TermType> move : legalMoves) {
			if (move.getKIFForm().equalsIgnoreCase(moveString)) {
				return move;
			}
		}
		
		throw new NoLegalMoveException("move \"" + moveString + "\" not found in legal moves!");
	}
}
