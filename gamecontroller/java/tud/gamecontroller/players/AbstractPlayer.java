/*
    Copyright (C) 2008-2010 Stephan Schiffel <stephan.schiffel@gmx.de>

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

import java.util.logging.Level;
import java.util.logging.Logger;

import tud.gamecontroller.ConnectionEstablishedNotifier;
import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.GameController;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.gamecontroller.term.TermInterface;

public abstract class AbstractPlayer<TermType extends TermInterface, StateType extends StateInterface<TermType, ? extends StateType>> implements Player<TermType, StateType> {

	protected RunnableMatchInterface<TermType, ?> match=null;
	protected RoleInterface<TermType> role=null;
	protected String name;
	private long runtime;
	private long startRunningTime;
	private long lastMessageRuntime;
	protected static final Logger logger = Logger.getLogger(GameController.class.getName());
		// it is important to use the same logger here that is used in the AbstractGameControllerRunner 
	
	private GDLVersion gdlVersion = null;

	public AbstractPlayer(String name, GDLVersion gdlVersion) {
		this.name = name;
		this.runtime = 0;
		this.lastMessageRuntime = 0;
		this.gdlVersion = gdlVersion;
	}
	
	public void gameStart(RunnableMatchInterface<TermType, StateType> match, RoleInterface<TermType> role, ConnectionEstablishedNotifier notifier) {
		this.match=match;
		this.role=role;
		this.runtime=0;
		this.lastMessageRuntime=0;
	}

	public void gameStop(Object seesTerms, ConnectionEstablishedNotifier notifier) {
		notifier.connectionEstablished();
	}
	
	protected void logErrorMessage(String type, String message) {
		GameControllerErrorMessage errorMessage = new GameControllerErrorMessage(type, message, this.getName());
		match.notifyErrorMessage(errorMessage);
		logger.log(Level.SEVERE, message, errorMessage);
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

	protected void setGdlVersion(GDLVersion gdlVersion) {
		this.gdlVersion = gdlVersion;
	}

	public String toString() {
		return "player("+name+")";
	}

}
