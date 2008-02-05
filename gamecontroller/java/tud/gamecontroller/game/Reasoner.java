package tud.gamecontroller.game;

import java.util.logging.Logger;

import cs227b.teamIago.gameProver.GameSimulator;
import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Expression;
import cs227b.teamIago.resolver.Predicate;
import cs227b.teamIago.util.GameState;

public class Reasoner {

	private GameSimulator gameSim;
	
	public Reasoner(String gameDescription) {
		gameSim=new GameSimulator(false, true);
		gameSim.ParseDescIntoTheory(gameDescription);
	}

	public boolean IsTerminal(GameState state) {
		synchronized (gameSim) {
			gameSim.SetGameState(state);
			return gameSim.IsTerminal();
		}
	}

	public ExpList GetRoles() {
		synchronized (gameSim) {
			return gameSim.GetRoles();
		}
	}

	public GameState SuccessorState(GameState state, ExpList movesList) {
		synchronized (gameSim) {
			gameSim.SetGameState(state);
			gameSim.SimulateStep(movesList);
			return gameSim.GetGameState();
		}
	}

	public boolean isLegal(Expression role, Expression move, GameState state) {
		synchronized (gameSim) {
			gameSim.SetGameState(state);
			ExpList roleVar=new ExpList();
			roleVar.add(role);
			roleVar.add(move);
			try {
				return gameSim.getTheory().findp(new Predicate(new Atom("LEGAL"),roleVar));
			} catch (InterruptedException e) {
				Logger logger=Logger.getLogger("tud.gamecontroller");
				logger.severe("reasoner was interrupted during findp("+new Predicate(new Atom("LEGAL"),roleVar)+"):");
				logger.severe(e.getMessage());
				return false;
			}
		}
	}

	public int GetGoalValue(Expression role, GameState state) {
		synchronized (gameSim) {
			gameSim.SetGameState(state);
			return gameSim.GetGoalValue(role);
		}
	}

	public ExpList GetLegalMoves(Expression role, GameState state) {
		synchronized (gameSim) {
			gameSim.SetGameState(state);
			return gameSim.GetLegalMoves(role);
		}
	}

	public GameState getInitialState() {
		synchronized (gameSim) {
			gameSim.SimulateStart();
			return gameSim.getTheory().getState();
		}
	}

}
