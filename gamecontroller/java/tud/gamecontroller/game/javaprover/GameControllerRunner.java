package tud.gamecontroller.game.javaprover;

import tud.gamecontroller.cli.AbstractGameControllerCLIRunner;
import tud.gamecontroller.game.Fluent;
import tud.gamecontroller.game.Game;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.MoveFactory;
import tud.gamecontroller.game.Role;
import tud.gamecontroller.game.State;
import cs227b.teamIago.util.GameState;

public class GameControllerRunner
		extends AbstractGameControllerCLIRunner<
			Role<Term>,
			Move<Term>,
			State<Role<Term>,Move<Term>,Fluent<Term>,GameState>,
			Game<Role<Term>,Move<Term>,Fluent<Term>,GameState>
		>{

	protected Game<Role<Term>,Move<Term>,Fluent<Term>,GameState> createGame(String gameDescription, String gameName) {
		Reasoner reasoner=new Reasoner(gameDescription);
		return new Game<Role<Term>,Move<Term>,Fluent<Term>,GameState>(gameDescription, gameName, reasoner);
	}

	public static void main(String[] args) {
		GameControllerRunner gcRunner=new GameControllerRunner();
		gcRunner.runFromCommandLine(args);
	}

	@Override
	protected MoveFactoryInterface<Move<Term>> getMoveFactory() {
		return new MoveFactory<Term>(new TermFactory());
	}
}
