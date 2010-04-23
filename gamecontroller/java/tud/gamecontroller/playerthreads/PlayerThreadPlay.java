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

package tud.gamecontroller.playerthreads;

import tud.gamecontroller.ConnectionEstablishedNotifier;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.term.TermInterface;

public class PlayerThreadPlay<
		TermType extends TermInterface,
		StateType extends StateInterface<TermType, ? extends StateType>
		> extends AbstractPlayerThread<TermType, StateType>
		implements ConnectionEstablishedNotifier {

	protected MoveInterface<TermType> move;
	protected Object seesTerms;
	
	public PlayerThreadPlay(RoleInterface<TermType> role, Player<TermType, StateType> player, RunnableMatchInterface<TermType, StateType> match, Object seesTerms, long deadline){
		super("PlayMessageThread("+player.getName()+","+match.getMatchID()+")", role, player, match, deadline);
		this.move=null;
		this.seesTerms = seesTerms;
	}
	public MoveInterface<TermType> getMove() {
		return move;
	}
	public void doRun(){
		move = player.gamePlay(seesTerms, this);
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[PlayerThreadPlay:");
		buffer.append(" name: ");
		buffer.append(getName());
		buffer.append(" match ID: ");
		buffer.append(match.getMatchID());
		buffer.append(" role: ");
		buffer.append(role);
		buffer.append(" player: ");
		buffer.append(player);
		//buffer.append(" priormoves: ");
		//buffer.append(priormoves);
		buffer.append(" seesFluents: ");
		buffer.append(seesTerms);
		buffer.append(" move: ");
		buffer.append(move);
		buffer.append("]");
		return buffer.toString();
	}
}
