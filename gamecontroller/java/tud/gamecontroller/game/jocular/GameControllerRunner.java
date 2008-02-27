package tud.gamecontroller.game.jocular;

import stanfordlogic.gdl.Parser;
import stanfordlogic.prover.ProofContext;
import tud.gamecontroller.cli.AbstractGameControllerCLIRunner;
import tud.gamecontroller.game.Fluent;
import tud.gamecontroller.game.Game;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.MoveFactory;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.Role;
import tud.gamecontroller.game.State;

public class GameControllerRunner
		extends AbstractGameControllerCLIRunner<
				Role<Term>,
				Move<Term>,
				State<Role<Term>,Move<Term>,Fluent<Term>,ProofContext>,
				Game<Role<Term>,Move<Term>,Fluent<Term>,ProofContext>
		>{
	private Parser parser; 
	
	public GameControllerRunner(){
		this.parser=new Parser();
	}
	
	protected Game<Role<Term>,Move<Term>,Fluent<Term>,ProofContext> createGame(String gameDescription, String gameName) {
		Reasoner reasoner=new Reasoner(gameDescription, parser);
		return new Game<Role<Term>,Move<Term>,Fluent<Term>,ProofContext>(gameDescription, gameName, reasoner);
	}

	public static void main(String[] args) {
		GameControllerRunner gcRunner=new GameControllerRunner();
		gcRunner.runFromCommandLine(args);
	}

	@Override
	protected MoveFactoryInterface<Move<Term>> getMoveFactory() {
		return new MoveFactory<Term>(new TermFactory(parser));
	}

}
