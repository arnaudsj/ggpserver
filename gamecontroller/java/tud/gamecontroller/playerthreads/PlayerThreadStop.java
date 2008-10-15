package tud.gamecontroller.playerthreads;

import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.term.TermInterface;

public class PlayerThreadStop<
		TermType extends TermInterface
		> extends AbstractPlayerThread<TermType> {

	private JointMoveInterface<TermType> priormoves;
	
	public PlayerThreadStop(RoleInterface<TermType> role, Player<TermType> player, MatchInterface<TermType, ?> match, JointMoveInterface<TermType> priormoves, long deadline){
		super(role, player, match, deadline);
		this.priormoves=priormoves;
	}
	public void run(){
		player.gameStop(priormoves, this);
	}
}
