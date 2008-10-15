package tud.gamecontroller.game.jocular;

import java.io.File;

import stanfordlogic.gdl.Parser;
import stanfordlogic.prover.ProofContext;
import tud.gamecontroller.game.ReasonerInterface;

public class GameControllerGuiRunner extends
		tud.gamecontroller.gui.AbstractGameControllerGuiRunner<Term, ProofContext> {

	private Parser parser; 
	
	public GameControllerGuiRunner(File gameFile) {
		super(gameFile);
		this.parser=new Parser();
	}

	@Override
	protected ReasonerInterface<Term, ProofContext> getReasoner(String gameDescription, String gameName) {
		return new Reasoner(gameDescription, parser);
	}
}
