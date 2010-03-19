/*
    Copyright (C) 2008,2009 Stephan Schiffel <stephan.schiffel@gmx.de>

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
import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.term.TermInterface;

public abstract class AbstractPlayer<TermType extends TermInterface, StateType extends StateInterface<TermType, ? extends StateType>> implements Player<TermType, StateType> {

	protected MatchInterface<TermType, ?> match=null;
	protected RoleInterface<TermType> role=null;
	private String name;
	private long runtime;
	private long startRunningTime;
	private long lastMessageRuntime;
	
	private GDLVersion gdlVersion = null;

	public AbstractPlayer(String name, GDLVersion gdlVersion) {
		this.name = name;
		this.runtime = 0;
		this.lastMessageRuntime = 0;
		this.gdlVersion = gdlVersion;
	}
	
	public void gameStart(MatchInterface<TermType, StateType> match, RoleInterface<TermType> role, ConnectionEstablishedNotifier notifier) {
		this.match=match;
		this.role=role;
		this.runtime=0;
		this.lastMessageRuntime=0;
	}

	public void gameStop(Object seesTerms, ConnectionEstablishedNotifier notifier) {
		notifier.connectionEstablished();
	}

	public long getTotalRuntime() {
		return runtime;
	}

	public long getLastMessageRuntime() {
		return lastMessageRuntime;
	}

	protected void notifyStartRunning() {
		startRunningTime=System.currentTimeMillis();
	}

	protected void notifyStopRunning() {
		lastMessageRuntime=System.currentTimeMillis()-startRunningTime;
		runtime+=lastMessageRuntime;
	}

	public String getName() {
		return name;
	}
	
	public GDLVersion getGdlVersion() {
		return this.gdlVersion;
	}

	public String toString() {
		return "player("+name+")";
	}

}
