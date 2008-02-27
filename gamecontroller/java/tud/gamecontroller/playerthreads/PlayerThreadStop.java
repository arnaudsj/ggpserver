package tud.gamecontroller.playerthreads;

import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.players.Player;

public class PlayerThreadStop<
		RoleType,
		MoveType
		> extends AbstractPlayerThread<RoleType, Player<? super RoleType, MoveType, ?>, Object> {

	private JointMoveInterface<? extends RoleType, ? extends MoveType> priormoves;
	
	public PlayerThreadStop(RoleType role, Player<? super RoleType, MoveType, ?> player, Object match, JointMoveInterface<? extends RoleType, ? extends MoveType> priormoves, long deadline){
		super(role, player, match, deadline);
		this.priormoves=priormoves;
	}
	public void run(){
		player.gameStop(priormoves, this);
	}
}
