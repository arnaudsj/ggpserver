package tud.gamecontroller.playerthreads;

import tud.gamecontroller.MessageSentNotifier;

public abstract class AbstractPlayerThread<
		RoleType,
		PlayerType,
		MatchType
		> extends Thread implements MessageSentNotifier {
	
	protected PlayerType player;
	protected RoleType role;
	protected MatchType match;
	private long deadline;
	private long timeout;
	private ChangeableBoolean messageSent;
	private ChangeableBoolean deadlineSet;
	
	public AbstractPlayerThread(RoleType role, PlayerType player, MatchType match, long timeout){
		this.role=role;
		this.player=player;
		this.match=match;
		this.timeout=timeout;
		deadline=0;
	}
	public PlayerType getPlayer() {
		return player;
	}
	public RoleType getRole(){
		return role;
	}
	public long getDeadLine() {
		return deadline;
	}
	
	public void start(){
		messageSent=new ChangeableBoolean(false);
		deadlineSet=new ChangeableBoolean(false);
		// make another thread that waits until the message is sent and sets the deadline for this thread
		new Thread(){
			public void run() {
				waitUntilMessageIsSent();
			}
		}.start();
		// start this thread
		super.start();
	}
	
	public abstract void run();
	
	private void waitUntilMessageIsSent() {
		synchronized (messageSent) {
			while (!messageSent.isTrue()){
				try {
					messageSent.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		deadline=System.currentTimeMillis()+timeout;
		synchronized (deadlineSet) {
			deadlineSet.setTrue();
			deadlineSet.notifyAll();
		}
	}
	
	public boolean waitUntilDeadline() {
		synchronized (deadlineSet) {
			while (!deadlineSet.isTrue()){
				try {
					deadlineSet.wait();
				} catch (InterruptedException e) {
				}
			}
		}
		long timeLeft=deadline-System.currentTimeMillis();
		if(timeLeft<=0){
			timeLeft=1;
		}
		if(isAlive()){
			try {
				join(timeLeft);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if(isAlive()){
			interrupt();
			return false;
		}else{
			return true;
		}
	}
	
	public void messageWasSent(){
		synchronized (messageSent) {
			messageSent.setTrue();
			messageSent.notifyAll();
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
	
}
