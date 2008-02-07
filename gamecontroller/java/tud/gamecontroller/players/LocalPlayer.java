package tud.gamecontroller.players;

import java.util.List;

import tud.gamecontroller.MessageSentNotifier;
import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.Role;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.TermInterface;

public abstract class LocalPlayer<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> extends AbstractPlayer<T,S> {
	
	protected S currentState=null;
	
	public LocalPlayer(String name) {
		super(name);
	}

	public void gameStart(Match<T,S> match, Role<T> role, MessageSentNotifier notifier) {
		super.gameStart(match, role, notifier);
		notifier.messageWasSent();
		currentState=match.getGame().getInitialState();
	}

	public Move<T> gamePlay(List<Move<T>> priormoves, MessageSentNotifier notifier) {
		notifier.messageWasSent();
		if(priormoves!=null){
			currentState=currentState.getSuccessor(priormoves);
		}
		return getNextMove();
	}

	protected abstract Move<T> getNextMove();
	
	public void gameStop(List<Move<T>> priormoves, MessageSentNotifier notifier) {
		super.gameStop(priormoves, notifier);
		notifier.messageWasSent();
	}

	public String toString(){
		return "local("+getName()+")";
	}
}
