package tud.gamecontroller.players;

import tud.gamecontroller.MessageSentNotifier;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.term.TermInterface;

public abstract class LocalPlayer<
	TermType extends TermInterface
	> extends AbstractPlayer<TermType> {
	
	protected StateInterface<TermType, ?> currentState=null;
	
	public LocalPlayer(String name) {
		super(name);
	}

	@Override
	public void gameStart(MatchInterface<TermType, ?> match, RoleInterface<TermType> role, MessageSentNotifier notifier) {
		super.gameStart(match, role, notifier);
		notifier.messageWasSent();
		currentState=match.getGame().getInitialState();
	}

	public MoveInterface<TermType> gamePlay(JointMoveInterface<TermType> jointMove, MessageSentNotifier notifier) {
		notifier.messageWasSent();
		if(jointMove!=null){
			currentState=currentState.getSuccessor(jointMove);
		}
		return getNextMove();
	}

	protected abstract MoveInterface<TermType> getNextMove();
	
	public String toString(){
		return "local("+getName()+")";
	}
}
