package tud.gamecontroller.game.javaprover;

import java.io.File;

import tud.gamecontroller.game.ReasonerInterface;
import cs227b.teamIago.util.GameState;

public class GameControllerGuiRunner extends
		tud.gamecontroller.gui.AbstractGameControllerGuiRunner<Term, GameState> {

	public GameControllerGuiRunner(File gameFile) {
		super(gameFile);
	}

	@Override
	protected ReasonerInterface<Term, GameState> getReasoner(String gameDescription, String gameName) {
		return new Reasoner(gameDescription);
	}
}
