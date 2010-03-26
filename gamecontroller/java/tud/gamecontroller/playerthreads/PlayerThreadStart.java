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

package tud.gamecontroller.playerthreads;

import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.term.TermInterface;

public class PlayerThreadStart<
	TermType extends TermInterface,
	StateType extends StateInterface<TermType, ? extends StateType>> extends AbstractPlayerThread<TermType, StateType> {

	public PlayerThreadStart(RoleInterface<TermType> role, Player<TermType, StateType> player, RunnableMatchInterface<TermType, StateType> match, long deadline){
		super("StartMessageThread("+player.getName()+","+match.getMatchID()+")",role, player, match, deadline);
	}
	
	public void doRun(){
		player.gameStart(match, getRole(), this);
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[PlayerThreadStart:");
		buffer.append(" name: ");
		buffer.append(getName());
		buffer.append(" match ID: ");
		buffer.append(match.getMatchID());
		buffer.append(" role: ");
		buffer.append(role);
		buffer.append(" player: ");
		buffer.append(player);
		buffer.append("]");
		return buffer.toString();
	}
}
