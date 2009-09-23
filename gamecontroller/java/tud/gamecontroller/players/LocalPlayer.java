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

package tud.gamecontroller.players;

import tud.gamecontroller.ConnectionEstablishedNotifier;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.term.TermInterface;

public abstract class LocalPlayer<
	TermType extends TermInterface
	> extends AbstractPlayer<TermType> {
	
	protected StateInterface<TermType, ?> currentState=null;
	
	public LocalPlayer(String name) {
		super(name);
	}

	@Override
	public void gameStart(MatchInterface<TermType, ?> match, RoleInterface<TermType> role, ConnectionEstablishedNotifier notifier) {
		super.gameStart(match, role, notifier);
		notifyStartRunning();
		notifier.connectionEstablished();
		currentState=match.getGame().getInitialState();
		notifyStopRunning();
	}

	public MoveInterface<TermType> gamePlay(JointMoveInterface<TermType> jointMove, ConnectionEstablishedNotifier notifier) {
		notifyStartRunning();
		notifier.connectionEstablished();
		if(jointMove!=null){
			currentState=currentState.getSuccessor(jointMove);
		}
		MoveInterface<TermType> move=getNextMove();
		notifyStopRunning();
		return move;
	}

	protected abstract MoveInterface<TermType> getNextMove();
	
	public String toString(){
		return "local("+getName()+")";
	}
}
