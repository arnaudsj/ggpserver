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

import tud.gamecontroller.MessageSentNotifier;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.term.TermInterface;

public abstract class AbstractPlayer<TermType extends TermInterface> implements Player<TermType> {

	protected MatchInterface<TermType, ?> match=null;
	protected RoleInterface<TermType> role=null;
	private String name;
	private long runtime;
	private long startRunningTime;

	public AbstractPlayer(String name){
		this.name=name;
		this.runtime=0;
	}
	
	public void gameStart(MatchInterface<TermType, ?> match, RoleInterface<TermType> role, MessageSentNotifier notifier) {
		this.match=match;
		this.role=role;
		this.runtime=0;
	}

	public void gameStop(JointMoveInterface<TermType> jointMove, MessageSentNotifier notifier) {
		notifier.messageWasSent();
	}

	public long getTotalRuntime() {
		return runtime;
	}
	
	protected void notifyStartRunning() {
		startRunningTime=System.currentTimeMillis();
	}

	protected void notifyStopRunning() {
		runtime+=System.currentTimeMillis()-startRunningTime;
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return "player("+name+")";
	}

}
