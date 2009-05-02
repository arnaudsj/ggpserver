package tud.ggpserver.scheduler;

import tud.gamecontroller.game.javaprover.Term;
import tud.ggpserver.datamodel.AbstractDBConnector;
import tud.ggpserver.datamodel.DBConnectorFactory;
import cs227b.teamIago.util.GameState;

public class RoundRobinScheduler extends AbstractRoundRobinScheduler<Term, GameState> {
	private static AbstractRoundRobinScheduler instance = null;
	
	private RoundRobinScheduler(AbstractDBConnector dbConnector) {
		super(dbConnector);
	}

	public static AbstractRoundRobinScheduler getInstance() {
		if (instance == null) {
			instance = new RoundRobinScheduler(DBConnectorFactory.getDBConnector());
		}
		return instance;
	}
}
