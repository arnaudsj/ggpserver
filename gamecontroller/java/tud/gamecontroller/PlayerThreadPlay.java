package tud.gamecontroller;

import java.util.List;

import tud.gamecontroller.game.Match;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.TermInterface;
import tud.gamecontroller.players.Player;

public class PlayerThreadPlay<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> extends AbstractPlayerThread<T,S> {

	private Move<T> move;
	private List<Move<T>> priormoves;
	
	public PlayerThreadPlay(int roleindex, Player<T,S> player, Match<T,S> match, List<Move<T>> priormoves){
		super(roleindex, player, match);
		this.priormoves=priormoves;
		this.move=null;
	}
	public Move<T> getMove() {
		return move;
	}
	public void run(){
		move=player.gamePlay(priormoves);
	}
}
