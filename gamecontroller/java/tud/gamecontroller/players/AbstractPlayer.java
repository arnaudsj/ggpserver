package tud.gamecontroller.players;

import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.Role;

public abstract class AbstractPlayer implements Player {

	protected Match match=null;
	protected Role role=null;
	private String name;

	public AbstractPlayer(String name){
		this.name=name;
	}
	
	public void gameStart(Match match, Role role) {
		this.match=match;
		this.role=role;
	}

	public void gameStop(Move[] priormoves) { }

	public String getName() {
		return name;
	}

	public String toString() {
		return "player("+name+")";
	}

}
