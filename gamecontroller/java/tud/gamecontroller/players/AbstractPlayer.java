package tud.gamecontroller.players;

import tud.gamecontroller.MessageSentNotifier;
import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.term.TermInterface;

public abstract class AbstractPlayer<TermType extends TermInterface> implements Player<TermType> {

	protected MatchInterface<TermType, ?> match=null;
	protected RoleInterface<TermType> role=null;
	private String name;

	public AbstractPlayer(String name){
		this.name=name;
	}
	
	public void gameStart(MatchInterface<TermType, ?> match, RoleInterface<TermType> role, MessageSentNotifier notifier) {
		this.match=match;
		this.role=role;
	}

	public void gameStop(JointMoveInterface<TermType> jointMove, MessageSentNotifier notifier) {
		notifier.messageWasSent();
	}

	public String getName() {
		return name;
	}

	public String toString() {
		return "player("+name+")";
	}

}
