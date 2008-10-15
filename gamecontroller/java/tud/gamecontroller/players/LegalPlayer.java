package tud.gamecontroller.players;

import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.term.TermInterface;

public class LegalPlayer<
	TermType extends TermInterface
	> extends LocalPlayer<TermType>  {

	public LegalPlayer(String name) {
		super(name);
	}

	public MoveInterface<TermType> getNextMove() {
		return currentState.getLegalMove(role);
	}
}
