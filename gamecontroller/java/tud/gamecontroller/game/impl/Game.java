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

import java.util.LinkedList;
import java.util.List;

import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.term.TermInterface;

public class Game<
	TermType extends TermInterface,
	ReasonerStateInfoType
	> implements GameInterface<
		TermType,
		State<TermType, ReasonerStateInfoType>> {

	private ReasonerInterface<TermType, ReasonerStateInfoType> reasoner;
	private String name;
	private List<RoleInterface<TermType>> orderedRoles=null;
		
	public Game(String gameDescription, String name, ReasonerInterface<TermType, ReasonerStateInfoType> reasoner) {
		this.name=name;
		this.reasoner=reasoner;
	}

	public State<TermType, ReasonerStateInfoType> getInitialState() {
		return new State<TermType,ReasonerStateInfoType>(reasoner, reasoner.getInitialState());
	}

	public RoleInterface<TermType> getRole(int roleindex) {
		return reasoner.GetRoles().get(roleindex);
	}

	public List<? extends RoleInterface<TermType>> getOrderedRoles(){
		if(orderedRoles==null){
			orderedRoles=new LinkedList<RoleInterface<TermType>>();
			for(int i=0;i<getNumberOfRoles();i++){
				orderedRoles.add(getRole(i));
			}
		}
		return orderedRoles;
	}

	public int getNumberOfRoles() {
		return reasoner.GetRoles().size();
	}

	public String getName() {
		return name;
	}

	public String getKIFGameDescription() {
		return reasoner.getKIFGameDescription();
	}


}