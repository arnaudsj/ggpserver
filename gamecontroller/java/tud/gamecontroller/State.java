package tud.gamecontroller;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.Connective;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Predicate;
import cs227b.teamIago.util.GameState;

public class State {
	private Reasoner reasoner;
	private GameState state;
	
	public State(Reasoner reasoner, GameState state) {
		this.reasoner=reasoner;
		this.state=state;
	}

	public boolean isTerminal() {
		return reasoner.IsTerminal(state);
	}

	public State getSuccessor(Move[] moves) {
		ExpList movesList=new ExpList();
		for(int i=0;i<moves.length; i++){
			ExpList doesArgs=new ExpList();
			doesArgs.add(reasoner.GetRoles().get(i));
			doesArgs.add(moves[i].expr);
			movesList.add(new Predicate(new Atom("DOES"), doesArgs));
		}
		return new State(reasoner, reasoner.SuccessorState(state, movesList));
	}

	public boolean isLegal(Role role, Move move) {
		return reasoner.isLegal(role.expr, move.expr, state);
	}

	public Move getLegalMove(Role role) {
		return getLegalMoves(role).iterator().next();
	}

	public int getGoalValue(Role role) {
		return reasoner.GetGoalValue(role.expr, state);
	}

	public Collection<Move> getLegalMoves(Role role) {
		ExpList exprlist=reasoner.GetLegalMoves(role.expr, state);
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

	@SuppressWarnings("unchecked")
	public Collection<Fluent> getFluents() {
		Collection<Fluent> fluents=new LinkedList<Fluent>();
		Iterator<ExpList> it=state.getMap().values().iterator();
		while(it.hasNext()){
			ExpList el=it.next();
			for(int i=0;i<el.size();i++){
				Predicate true_expr=(Predicate)el.get(i);
				 fluents.add(new Fluent(true_expr.getOperands().get(0)));
			}
		}
		return fluents;
	}

}
