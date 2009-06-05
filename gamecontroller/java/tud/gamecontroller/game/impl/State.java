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

package tud.gamecontroller.game.impl;

import java.util.Collection;

import tud.gamecontroller.game.FluentInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.term.GameObjectInterface;
import tud.gamecontroller.term.TermInterface;

public class State<
	TermType extends TermInterface,
	ReasonerStateInfoType
	> implements StateInterface<
			TermType,
			State<TermType, ReasonerStateInfoType>> {

	protected ReasonerInterface<TermType, ReasonerStateInfoType> reasoner;
	protected ReasonerStateInfoType stateInformation; 

	public State(ReasonerInterface<TermType, ReasonerStateInfoType> reasoner, ReasonerStateInfoType stateInformation){
		this.reasoner=reasoner;
		this.stateInformation=stateInformation;
	}
	
	public boolean isTerminal() {
		return reasoner.isTerminal(stateInformation);
	}

	public State<TermType, ReasonerStateInfoType> getSuccessor(JointMoveInterface<TermType> jointMove) {
		return new State<TermType, ReasonerStateInfoType>(reasoner, reasoner.getSuccessorState(stateInformation, jointMove));
	}

	public boolean isLegal(RoleInterface<TermType> role, MoveInterface<TermType> move) {
		return reasoner.isLegal(stateInformation, role, move);
	}

	public int getGoalValue(RoleInterface<TermType> role) {
		return reasoner.getGoalValue(stateInformation, role);
	}

	public MoveInterface<TermType> getLegalMove(RoleInterface<TermType> role) {
		return getLegalMoves(role).iterator().next();
	}

	public Collection<? extends MoveInterface<TermType>> getLegalMoves(RoleInterface<TermType> role) {
		return reasoner.getLegalMoves(stateInformation, role);
	}
	
	public Collection<? extends FluentInterface<TermType>> getFluents() {
		return reasoner.getFluents(stateInformation);
	}

	public String toString(){
		StringBuilder sb=new StringBuilder();
		sb.append('(');
		for(GameObjectInterface f:getFluents()){
			sb.append(f.getKIFForm());
			sb.append(" ");
		}
		sb.append(')');
		return sb.toString();
	}
}
