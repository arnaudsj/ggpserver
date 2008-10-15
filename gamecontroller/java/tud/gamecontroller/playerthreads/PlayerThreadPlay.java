package tud.gamecontroller.playerthreads;

import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.players.Player;
import tud.gamecontroller.term.TermInterface;

public class PlayerThreadPlay<
		TermType extends TermInterface
		> extends AbstractPlayerThread<TermType> {

	private MoveInterface<TermType> move;
	private JointMoveInterface<TermType> priormoves;
	
	public PlayerThreadPlay(RoleInterface<TermType> role, Player<TermType> player, MatchInterface<TermType, ?> match, JointMoveInterface<TermType> priormoves, long deadline){
		super(role, player, match, deadline);
		this.priormoves=priormoves;
		this.move=null;
	}
	public MoveInterface<TermType> getMove() {
		return move;
	}
	public void run(){
		move=player.gamePlay(priormoves, this);
	}
}
