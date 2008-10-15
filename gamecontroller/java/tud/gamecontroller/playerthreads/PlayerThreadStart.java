package tud.gamecontroller.playerthreads;

import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.term.TermInterface;

public class PlayerThreadStart<
	TermType extends TermInterface> extends AbstractPlayerThread<TermType> {

	public PlayerThreadStart(RoleInterface<TermType> role, Player<TermType> player, MatchInterface<TermType, ?> match, long deadline){
		super(role, player, match, deadline);
	}
	
	public void run(){
		player.gameStart(match, getRole(), this);
	}
}
