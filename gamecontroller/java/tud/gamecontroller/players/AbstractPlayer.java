package tud.gamecontroller.players;

import java.util.List;

import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.Role;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.TermInterface;

public abstract class AbstractPlayer<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> implements Player<T,S> {

	protected Match<T,S> match=null;
	protected Role<T> role=null;
	private String name;

	public AbstractPlayer(String name){
		this.name=name;
	}
	
	public void gameStart(Match<T,S> match, Role<T> role) {
		this.match=match;
		this.role=role;
	}

	public void gameStop(List<Move<T>> priormoves) { }

	public String getName() {
		return name;
	}

	public String toString() {
		return "player("+name+")";
	}

}
