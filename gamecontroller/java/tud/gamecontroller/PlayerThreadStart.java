package tud.gamecontroller;

import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.TermInterface;
import tud.gamecontroller.players.Player;

public class PlayerThreadStart<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> extends AbstractPlayerThread<T,S> {

	public PlayerThreadStart(int roleindex, Player<T,S> player, Match<T,S> match){
		super(roleindex, player, match);
	}
	public void run(){
		player.gameStart(match, match.getGame().getRole(roleindex));
	}
}
