package tud.gamecontroller.game.jocular;

import stanfordlogic.prover.ProofContext;
import tud.gamecontroller.GameController;
import tud.gamecontroller.game.impl.Match;

public class JocularGameController extends GameController<Term, ProofContext> {

	public JocularGameController(Match<Term, ProofContext> match) {
		super(match);
	}

}
