package tud.gamecontroller.players;

import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.Role;
import tud.gamecontroller.game.State;

public abstract class LocalPlayer extends AbstractPlayer {
	
	protected State currentState=null;
	
	public LocalPlayer(String name) {
		super(name);
	}

	public void gameStart(Match match, Role role) {
		super.gameStart(match, role);
		currentState=match.getGame().getInitialState();
	}

	public Move gamePlay(Move[] priormoves) {
		if(priormoves!=null){
			currentState=currentState.getSuccessor(priormoves);
		}
		return getNextMove();
	}

	protected abstract Move getNextMove();
	
	public String toString(){
		return "local("+getName()+")";
	}
}
