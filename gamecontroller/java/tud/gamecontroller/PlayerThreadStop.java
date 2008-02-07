package tud.gamecontroller;

import java.util.List;

import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.TermInterface;
import tud.gamecontroller.players.Player;

public class PlayerThreadStop<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> extends AbstractPlayerThread<T,S> {

	private List<Move<T>> priormoves;
	
	public PlayerThreadStop(int roleindex, Player<T,S> player, Match<T,S> match, List<Move<T>> priormoves, long deadline){
		super(roleindex, player, match, deadline);
		this.priormoves=priormoves;
	}
	public void run(){
		player.gameStop(priormoves, this);
	}
}
