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
import java.util.List;

import tud.gamecontroller.auxiliary.InvalidKIFException;

public interface ReasonerInterface<
	TermType,
	ReasonerStateInfoType> {

	List<? extends RoleInterface<TermType>> getRoles();

	ReasonerStateInfoType getInitialState();

	boolean isTerminal(ReasonerStateInfoType state);

	ReasonerStateInfoType getSuccessorState(ReasonerStateInfoType state, JointMoveInterface<TermType> jointMove);

	boolean isLegal(ReasonerStateInfoType state, RoleInterface<TermType> role, MoveInterface<TermType> move);

	int getGoalValue(ReasonerStateInfoType state, RoleInterface<TermType> role);

	Collection<? extends MoveInterface<TermType>> getLegalMoves(ReasonerStateInfoType state, RoleInterface<TermType> role);

	Collection<? extends FluentInterface<TermType>> getFluents(ReasonerStateInfoType state);
	
	public Collection<TermType> getSeesTerms(ReasonerStateInfoType state, RoleInterface<TermType> role, JointMoveInterface<TermType> jointMove);
	
	public Collection<TermType> getSeesXMLTerms(ReasonerStateInfoType state, RoleInterface<TermType> role);
	
	String getKIFGameDescription();
	
	public ReasonerStateInfoType getStateFromString(String state) throws InvalidKIFException;
}