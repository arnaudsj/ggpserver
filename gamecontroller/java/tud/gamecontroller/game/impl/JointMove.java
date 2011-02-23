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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.term.GameObjectInterface;
import tud.gamecontroller.term.TermInterface;

public class JointMove<TermType extends TermInterface>
	extends HashMap<RoleInterface<TermType>, MoveInterface<TermType>>
	implements JointMoveInterface<TermType> {
	
	private List<? extends RoleInterface<TermType>> orderedRoles;
	
	public JointMove(List<? extends RoleInterface<TermType>> orderedRoles){
		super();
		this.orderedRoles=orderedRoles;
	}

	public JointMove(List<? extends RoleInterface<TermType>> orderedRoles, Map<RoleInterface<TermType>, MoveInterface<TermType>> moveMap){
		super(moveMap);
		this.orderedRoles=orderedRoles;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 7570393446222872482L;
	
	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.JointMoveInterface#getOrderedMoves()
	 */
	public List<MoveInterface<TermType>> getOrderedMoves(){
		List<MoveInterface<TermType>> moves=new LinkedList<MoveInterface<TermType>>();
		for(RoleInterface<TermType> role:orderedRoles){
			moves.add(get(role));
		}
		return moves;
	}

	/* (non-Javadoc)
	 * @see tud.gamecontroller.game.JointMoveInterface#getKIFForm()
	 */
	public String getKIFForm() {
		StringBuilder sb=new StringBuilder();
		sb.append('(');
		boolean first=true;
		for(GameObjectInterface m:getOrderedMoves()){
			if(first){
				first=false;
			}else{
				sb.append(' ');
			}
			sb.append(m.getKIFForm());
		}
		sb.append(')');
		return sb.toString();
	}
}
