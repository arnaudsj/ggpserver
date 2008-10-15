package tud.gamecontroller.game.javaprover;

import tud.gamecontroller.cli.AbstractGameControllerCLIRunner;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.impl.Move;
import tud.gamecontroller.game.impl.MoveFactory;
import cs227b.teamIago.util.GameState;

public class GameControllerCLIRunner
		extends AbstractGameControllerCLIRunner<
			Term,
			GameState
		>{

	public static void main(String[] args) {
		GameControllerCLIRunner gcRunner=new GameControllerCLIRunner();
		gcRunner.runFromCommandLine(args);
	}

	@Override
	protected MoveFactoryInterface<Move<Term>> getMoveFactory() {
		return new MoveFactory<Term>(new TermFactory());
	}

	@Override
	protected ReasonerInterface<Term, GameState> getReasoner(String gameDescription, String gameName) {
		return new Reasoner(gameDescription);
	}
}
