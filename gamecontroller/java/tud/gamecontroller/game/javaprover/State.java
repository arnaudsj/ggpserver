package tud.gamecontroller.game.javaprover;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import tud.gamecontroller.game.Fluent;
import tud.gamecontroller.game.Move;
import tud.gamecontroller.game.Role;
import tud.gamecontroller.game.StateInterface;


import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.Connective;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Predicate;
import cs227b.teamIago.util.GameState;

public class State implements StateInterface<Term, State> {
	private Reasoner reasoner;
	private GameState state;
	
	public State(Reasoner reasoner, GameState state) {
		this.reasoner=reasoner;
		this.state=state;
	}

	public boolean isTerminal() {
		return reasoner.IsTerminal(state);
	}

	public State getSuccessor(List<Move<Term>> moves) {
		ExpList movesList=new ExpList();
		for(int i=0; i<moves.size(); i++){
			ExpList doesArgs=new ExpList();
			doesArgs.add(reasoner.GetRoles().get(i));
			doesArgs.add(moves.get(i).getTerm().getExpr());
			movesList.add(new Predicate(new Atom("DOES"), doesArgs));
		}
		return new State(reasoner, reasoner.SuccessorState(state, movesList));
	}

	public boolean isLegal(Role<Term> role, Move<Term> move) {
		return reasoner.isLegal(role.getTerm().getExpr(), move.getTerm().getExpr(), state);
	}

	public Move<Term> getLegalMove(Role<Term> role) {
		return getLegalMoves(role).iterator().next();
	}

	public int getGoalValue(Role<Term> role) {
		return reasoner.GetGoalValue(role.getTerm().getExpr(), state);
	}

	public Collection<Move<Term>> getLegalMoves(Role<Term> role) {
		ExpList exprlist=reasoner.GetLegalMoves(role.getTerm().getExpr(), state);
		Collection<Move<Term>> moveslist=new LinkedList<Move<Term>>();
		for(int i=0;i<exprlist.size();i++){
			Move<Term> move=new Move<Term>(new Term(((Connective)exprlist.get(i)).getOperands().get(1)));
			moveslist.add(move);
		}
		return moveslist;
	}
	
	public String toString(){
		return state.toString();
	}

	@SuppressWarnings("unchecked")
	public Collection<Fluent<Term>> getFluents() {
		Collection<Fluent<Term>> fluents=new LinkedList<Fluent<Term>>();
		Iterator<ExpList> it=state.getMap().values().iterator();
		while(it.hasNext()){
			ExpList el=it.next();
			for(int i=0;i<el.size();i++){
				Predicate true_expr=(Predicate)el.get(i);
				 fluents.add(new Fluent<Term>(new Term(true_expr.getOperands().get(0))));
			}
		}
		return fluents;
	}

}
