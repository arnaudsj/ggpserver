package tud.gamecontroller.players;

import tud.aux.NamedObject;
import tud.gamecontroller.MessageSentNotifier;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;

public interface Player<TermType> extends NamedObject{
	public void gameStart(MatchInterface<TermType, ?> match, RoleInterface<TermType> role, MessageSentNotifier notifier);
	public MoveInterface<TermType> gamePlay(JointMoveInterface<TermType> jointMove, MessageSentNotifier notifier);
	public void gameStop(JointMoveInterface<TermType> jointMove, MessageSentNotifier notifier);
}
