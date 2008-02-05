package tud.gamecontroller.players;

import tud.gamecontroller.game.Move;

public class LegalPlayer extends LocalPlayer {

	public LegalPlayer(String name) {
		super(name);
	}

	public Move getNextMove() {
		return currentState.getLegalMove(role);
	}
}
