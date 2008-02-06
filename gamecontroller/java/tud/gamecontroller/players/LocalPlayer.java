package tud.gamecontroller.players;

import java.util.List;

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

	public void gameStart(Match<T,S> match, Role<T> role) {
		super.gameStart(match, role);
		currentState=match.getGame().getInitialState();
	}

	public Move<T> gamePlay(List<Move<T>> priormoves) {
		if(priormoves!=null){
			currentState=currentState.getSuccessor(priormoves);
		}
		return getNextMove();
	}

	protected abstract Move<T> getNextMove();
	
	public String toString(){
		return "local("+getName()+")";
	}
}
