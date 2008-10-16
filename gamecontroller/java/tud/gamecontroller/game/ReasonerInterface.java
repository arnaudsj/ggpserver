/*
    Copyright (C) 2008 Stephan Schiffel <stephan.schiffel@gmx.de>

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
import java.util.List;

public interface ReasonerInterface<
	TermType,
	ReasonerStateInfoType> {

	List<? extends RoleInterface<TermType>> GetRoles();

	ReasonerStateInfoType getInitialState();

	boolean isTerminal(ReasonerStateInfoType state);

	ReasonerStateInfoType getSuccessorState(ReasonerStateInfoType state, JointMoveInterface<TermType> jointMove);

	boolean isLegal(ReasonerStateInfoType state, RoleInterface<TermType> role, MoveInterface<TermType> move);

	int GetGoalValue(ReasonerStateInfoType state, RoleInterface<TermType> role);

	Collection<? extends MoveInterface<TermType>> GetLegalMoves(ReasonerStateInfoType state, RoleInterface<TermType> role);

	Collection<? extends FluentInterface<TermType>> getFluents(ReasonerStateInfoType state);

	String getKIFGameDescription();

}