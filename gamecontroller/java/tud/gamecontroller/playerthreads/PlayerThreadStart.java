package tud.gamecontroller.playerthreads;

import tud.gamecontroller.players.Player;

public class PlayerThreadStart<
		RoleType,
		MatchType
		> extends AbstractPlayerThread<RoleType, Player<? super RoleType, ?, ? super MatchType>, MatchType> {

	public PlayerThreadStart(RoleType role, Player<? super RoleType, ?, ? super MatchType> player, MatchType match, long deadline){
		super(role, player, match, deadline);
	}
	public void run(){
		player.gameStart(match, getRole(), this);
	}
}
