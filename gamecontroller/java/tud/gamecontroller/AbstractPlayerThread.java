package tud.gamecontroller;

import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.TermInterface;
import tud.gamecontroller.players.Player;

public abstract class AbstractPlayerThread<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> extends Thread {
	
	protected Player<T,S> player;
	protected int roleindex;
	protected Match<T,S> match;
	private long deadline;
	
	public AbstractPlayerThread(int roleindex, Player<T,S> player, Match<T,S> match){
		this.roleindex=roleindex;
		this.player=player;
		this.match=match;
		deadline=0;
	}
	public Player<T,S> getPlayer() {
		return player;
	}
	public int getRoleIndex(){
		return roleindex;
	}
	public long getDeadLine() {
		return deadline;
	}
	public void setDeadLine(long deadline) {
		this.deadline=deadline;
	}
	public abstract void run();
}
