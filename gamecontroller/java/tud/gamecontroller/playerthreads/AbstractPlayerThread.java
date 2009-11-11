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

package tud.gamecontroller.playerthreads;

// import java.util.logging.Logger;

import tud.gamecontroller.ConnectionEstablishedNotifier;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.term.TermInterface;

public abstract class AbstractPlayerThread<
		TermType extends TermInterface
		> extends Thread implements ConnectionEstablishedNotifier {
	// private static final Logger logger = Logger.getLogger(AbstractPlayerThread.class.getName());
	
	protected Player<TermType> player;
	protected RoleInterface<TermType> role;
	protected MatchInterface<TermType, ?> match;
	private long deadline;
	private long timeout;
	private ChangeableBoolean connectionEstablished;
	private ChangeableBoolean deadlineSet;
	
	public AbstractPlayerThread(String threadName, RoleInterface<TermType> role, Player<TermType> player, MatchInterface<TermType, ?> match, long timeout){
		super(threadName);
		this.role=role;
		this.player=player;
		this.match=match;
		this.timeout=timeout;
		deadline=0;
		// logger.info("Player thread initialized: " + this); // this.toString() will miss properties that are initialized in subclasses
	}
	public Player<TermType> getPlayer() {
		return player;
	}
	public RoleInterface<TermType> getRole(){
		return role;
	}
	public long getDeadLine() {
		return deadline;
	}
	
	public void start(){
		connectionEstablished=new ChangeableBoolean(false);
		deadlineSet=new ChangeableBoolean(false);
		// make another thread that waits until the message is sent and sets the deadline for this thread
		new Thread(){
			public void run() {
				waitUntilConnectionIsEstablished();
			}
		}.start();
		// start this thread
		super.start();
	}
	
	public abstract void run();
	
	private void waitUntilConnectionIsEstablished() {
		try {
			synchronized (connectionEstablished) {
				while (!connectionEstablished.isTrue()){
					connectionEstablished.wait();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		deadline=System.currentTimeMillis()+timeout;
		synchronized (deadlineSet) {
			deadlineSet.setTrue();
			deadlineSet.notifyAll();
		}
	}
	
	public boolean waitUntilDeadline() throws InterruptedException {
		synchronized (deadlineSet) {
			while (!deadlineSet.isTrue()){
				deadlineSet.wait();
			}
		}
		long timeLeft=deadline-System.currentTimeMillis();
		if(timeLeft<=0){
			timeLeft=1;
		}
		if(isAlive()){
			join(timeLeft);
		}
		if(isAlive()){
			interrupt();
			return false;
		}else{
			return true;
		}
	}
	
	public void connectionEstablished(){
		synchronized (connectionEstablished) {
			connectionEstablished.setTrue();
			connectionEstablished.notifyAll();
		}
	}

	private class ChangeableBoolean{
		private boolean value;
		public ChangeableBoolean(boolean value){
			this.value=value;
		}
		public void setTrue(){
			this.value=true;
		}
		public void setFalse(){
			this.value=false;
		}
		public boolean isTrue(){
			return value;
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		try {
			// logger.info("Player thread finalized: " + this); //$NON-NLS-1$
		} finally {
			super.finalize();
		}
	}
}
