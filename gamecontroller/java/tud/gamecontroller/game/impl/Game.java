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
	private String stylesheet = null;  // this can remain null (no stylesheet will be used)
		
	public Game(String gameDescription, String name, ReasonerInterface<TermType, ReasonerStateInfoType> reasoner) {
		this.name=name;
		this.reasoner=reasoner;
	}

	public Game(String gameDescription, String name, ReasonerInterface<TermType, ReasonerStateInfoType> reasoner, String stylesheet) {
		this(gameDescription, name, reasoner);
		this.stylesheet = stylesheet;
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

	@Override
	public int hashCode() {
		final int PRIME = 31;
		int result = 1;
		result = PRIME * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	/**
	 * Two games are considered equal iff their names match (i.e., name is a unique identifier).
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final Game other = (Game) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	public String getStylesheet() {
		return stylesheet ;
	}
}
