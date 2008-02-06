package tud.gamecontroller.players;

import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.game.TermInterface;

public class LegalPlayer<
		T extends TermInterface,
		S extends StateInterface<T,S>
		> extends LocalPlayer<T,S>  {

	public LegalPlayer(String name) {
		super(name);
	}

	public Move<T> getNextMove() {
		return currentState.getLegalMove(role);
	}
}
