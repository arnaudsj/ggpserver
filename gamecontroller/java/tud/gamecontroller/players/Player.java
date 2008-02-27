package tud.gamecontroller.players;

import tud.aux.NamedObject;
import tud.gamecontroller.MessageSentNotifier;
import tud.gamecontroller.game.JointMoveInterface;

public interface Player<
		RoleType,
		MoveType,
		MatchType
		> extends NamedObject{
	public void gameStart(MatchType match, RoleType role, MessageSentNotifier notifier);
	public MoveType gamePlay(JointMoveInterface<? extends RoleType, ? extends MoveType> jointMove, MessageSentNotifier notifier);
	public void gameStop(JointMoveInterface<? extends RoleType, ? extends MoveType> jointMove, MessageSentNotifier notifier);
}
