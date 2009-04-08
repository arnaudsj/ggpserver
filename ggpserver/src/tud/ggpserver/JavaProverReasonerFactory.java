package tud.ggpserver;

import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.impl.Move;
import tud.gamecontroller.game.impl.MoveFactory;
import tud.gamecontroller.game.javaprover.Reasoner;
import tud.gamecontroller.game.javaprover.Term;
import tud.gamecontroller.game.javaprover.TermFactory;
import cs227b.teamIago.util.GameState;


public class JavaProverReasonerFactory extends AbstractReasonerFactory<Term, GameState> {
	
	private static JavaProverReasonerFactory instance;
	
	static {
		instance = new JavaProverReasonerFactory();
	}

	public static JavaProverReasonerFactory getInstance() {		
		return instance;
	}
	
	public MoveFactoryInterface<Move<Term>> getMoveFactory() {
		return new MoveFactory<Term>(new TermFactory());
	}

	public ReasonerInterface<Term, GameState> getReasoner(String gameDescription, String gameName) {
		return new Reasoner(gameDescription);
	}	
}
