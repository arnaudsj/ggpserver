package tud.gamecontroller;

import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.TermInterface;
import tud.gamecontroller.players.Player;

public class PlayerThreadStart<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> extends AbstractPlayerThread<T,S> {

	public PlayerThreadStart(int roleindex, Player<T,S> player, Match<T, S, Player<T,S>> match, long deadline){
		super(roleindex, player, match, deadline);
	}
	public void run(){
		player.gameStart(match, match.getGame().getRole(roleindex), this);
	}
}
