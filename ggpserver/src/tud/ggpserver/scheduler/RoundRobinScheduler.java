package tud.ggpserver.scheduler;

import tud.gamecontroller.game.javaprover.Term;
import tud.ggpserver.AbstractReasonerFactory;
import tud.ggpserver.JavaProverReasonerFactory;
import tud.ggpserver.datamodel.DBConnector;
import cs227b.teamIago.util.GameState;

public class RoundRobinScheduler extends AbstractRoundRobinScheduler<Term, GameState> {
	private static AbstractRoundRobinScheduler instance = null;
	
	private DBConnector<Term, GameState> db;

	
	public static AbstractRoundRobinScheduler getInstance() {
		if (instance == null) {
			instance = new RoundRobinScheduler();
		}
		return instance;
	}
	
	@Override
	protected DBConnector<Term, GameState> getDBConnector() {
		if (db == null) {
			 db = new DBConnector<Term, GameState>();
		}
		return db;
	}

	@Override
	protected AbstractReasonerFactory<Term, GameState> getReasonerFactory() {
		return JavaProverReasonerFactory.getInstance();
	}
}
