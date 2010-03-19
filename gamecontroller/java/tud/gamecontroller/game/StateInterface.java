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

package tud.gamecontroller.game;

import java.util.Collection;

public interface StateInterface<
	TermType,
	StateType extends StateInterface<TermType, StateType>> {

	boolean isTerminal();

	StateType getSuccessor(JointMoveInterface<TermType> jointmove);

	boolean isLegal(RoleInterface<TermType> role, MoveInterface<TermType> move);

	MoveInterface<TermType> getLegalMove(RoleInterface<TermType> role);

	int getGoalValue(RoleInterface<TermType> role);

	Collection<? extends MoveInterface<TermType>> getLegalMoves(RoleInterface<TermType> role);

	Collection<? extends FluentInterface<TermType>> getFluents();
	
	Collection<TermType> getSeesTerms(RoleInterface<TermType> role, JointMoveInterface<TermType> jointMove);
	
	Collection<TermType> getSeesXMLTerms(RoleInterface<TermType> role);

	/**
	 * returns the list of fluents as a string in infix KIF notation
	 */
	public String toString();
}
