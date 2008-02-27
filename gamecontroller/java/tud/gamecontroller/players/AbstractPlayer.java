package tud.gamecontroller.players;

import tud.gamecontroller.MessageSentNotifier;
import tud.gamecontroller.game.JointMoveInterface;

public abstract class AbstractPlayer<
	RoleType,
	MoveType,
	MatchType
	> implements Player<RoleType, MoveType, MatchType> {

	protected MatchType match=null;
	protected RoleType role=null;
	private String name;

	public AbstractPlayer(String name){
		this.name=name;
	}
	
	public void gameStart(MatchType match, RoleType role, MessageSentNotifier notifier) {
		this.match=match;
		this.role=role;
	}

	public void gameStop(JointMoveInterface<? extends RoleType, ? extends MoveType> jointMove, MessageSentNotifier notifier) {
		notifier.messageWasSent();
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return "player("+name+")";
	}

}
