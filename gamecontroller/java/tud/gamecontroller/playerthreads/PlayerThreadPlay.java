package tud.gamecontroller.playerthreads;

import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.players.Player;

public class PlayerThreadPlay<
		RoleType,
		MoveType
		> extends AbstractPlayerThread<RoleType, Player<? super RoleType, MoveType, ?>, Object> {

	private MoveType move;
	private JointMoveInterface<? extends RoleType, ? extends MoveType> priormoves;
	
	public PlayerThreadPlay(RoleType role, Player<? super RoleType, MoveType, ?> player, Object match, JointMoveInterface<? extends RoleType, ? extends MoveType> priormoves, long deadline){
		super(role, player, match, deadline);
		this.priormoves=priormoves;
		this.move=null;
	}
	public MoveType getMove() {
		return move;
	}
	public void run(){
		move=player.gamePlay(priormoves, this);
	}
}
