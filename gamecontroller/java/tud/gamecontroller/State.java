package tud.gamecontroller;

import java.util.Collection;
import java.util.LinkedList;

import cs227b.teamIago.gameProver.GameSimulator;
import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.Connective;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Predicate;
import cs227b.teamIago.util.GameState;

public class State {
	private GameSimulator gameSim;
	private GameState state;
	
	public State(GameSimulator gameSim, GameState state) {
		this.gameSim=gameSim;
		this.state=state;
	}

	public boolean isTerminal() {
		gameSim.SetGameState(state);
		return gameSim.IsTerminal();
	}

	public State getSuccessor(Move[] moves) {
		gameSim.SetGameState(state);
		ExpList movesList=new ExpList();
		for(int i=0;i<moves.length; i++){
			ExpList doesArgs=new ExpList();
			doesArgs.add(gameSim.GetRoles().get(i));
			doesArgs.add(moves[i].expr);
			movesList.add(new Predicate(new Atom("DOES"), doesArgs));
		}
		gameSim.SimulateStep(movesList);
		return new State(gameSim, gameSim.GetGameState());
	}

	public boolean isLegal(Role role, Move move) {
		gameSim.SetGameState(state);
		ExpList roleVar=new ExpList();
		roleVar.add(role.expr);
		roleVar.add(move.expr);
		try {
			return gameSim.getTheory().findp(new Predicate(new Atom("LEGAL"),roleVar));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}

	public Move getLegalMove(Role role) {
		return getLegalMoves(role).iterator().next();
	}

	public int getGoalValue(Role role) {
		gameSim.SetGameState(state);
		return gameSim.GetGoalValue(role.expr);
	}

	public Collection<Move> getLegalMoves(Role role) {
		gameSim.SetGameState(state);
		ExpList exprlist=gameSim.GetLegalMoves(role.expr);
		LinkedList<Move> moveslist=new LinkedList<Move>();
		for(int i=0;i<exprlist.size();i++){
			Move move=new Move(((Connective)exprlist.get(i)).getOperands().get(1));
			moveslist.add(move);
		}
		return moveslist;
	}
	
	public String toString(){
		return state.toString();
	}

}
