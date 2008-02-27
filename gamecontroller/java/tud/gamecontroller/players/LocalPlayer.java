package tud.gamecontroller.players;

import tud.gamecontroller.MessageSentNotifier;
import tud.gamecontroller.game.GameInterface;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.StateInterface;

public abstract class LocalPlayer<
	RoleType,
	MoveType 
	> extends AbstractPlayer<
		RoleType,
		MoveType,
		MatchInterface<RoleType, ? extends GameInterface<?, ? extends StateInterface<RoleType, MoveType, ?, ?>>, ?>
		> {
	
	protected StateInterface<RoleType, MoveType, ?, ?> currentState=null;
	
	public LocalPlayer(String name) {
		super(name);
	}

	@Override
	public void gameStart(MatchInterface<RoleType, ? extends GameInterface<?, ? extends StateInterface<RoleType, MoveType, ?, ?>>, ?> match, RoleType role, MessageSentNotifier notifier) {
		super.gameStart(match, role, notifier);
		notifier.messageWasSent();
		currentState=match.getGame().getInitialState();
	}

	public MoveType gamePlay(JointMoveInterface<? extends RoleType, ? extends MoveType> jointMove, MessageSentNotifier notifier) {
		notifier.messageWasSent();
		if(jointMove!=null){
			currentState=currentState.getSuccessor(jointMove);
		}
		return getNextMove();
	}

	protected abstract MoveType getNextMove();
	
	public String toString(){
		return "local("+getName()+")";
	}
}
