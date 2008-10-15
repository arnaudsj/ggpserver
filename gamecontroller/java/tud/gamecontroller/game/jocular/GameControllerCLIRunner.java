package tud.gamecontroller.game.jocular;

import stanfordlogic.gdl.Parser;
import stanfordlogic.prover.ProofContext;
import tud.gamecontroller.cli.AbstractGameControllerCLIRunner;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.impl.Move;
import tud.gamecontroller.game.impl.MoveFactory;

public class GameControllerCLIRunner
		extends AbstractGameControllerCLIRunner<Term, ProofContext>{
	private Parser parser; 
	
	public GameControllerCLIRunner(){
		this.parser=new Parser();
	}
	
	public static void main(String[] args) {
		GameControllerCLIRunner gcRunner=new GameControllerCLIRunner();
		gcRunner.runFromCommandLine(args);
	}

	@Override
	protected MoveFactoryInterface<Move<Term>> getMoveFactory() {
		return new MoveFactory<Term>(new TermFactory(parser));
	}

	@Override
	protected ReasonerInterface<Term, ProofContext> getReasoner(String gameDescription, String gameName) {
		return new Reasoner(gameDescription, parser);
	}
}
