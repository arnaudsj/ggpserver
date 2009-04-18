package tud.ggpserver.scheduler;

import tud.gamecontroller.game.javaprover.Term;
import cs227b.teamIago.util.GameState;

public class RoundRobinScheduler extends AbstractRoundRobinScheduler<Term, GameState> {
	private static AbstractRoundRobinScheduler instance = null;
	
	public static AbstractRoundRobinScheduler getInstance() {
		if (instance == null) {
			instance = new RoundRobinScheduler();
		}
		return instance;
	}
}
