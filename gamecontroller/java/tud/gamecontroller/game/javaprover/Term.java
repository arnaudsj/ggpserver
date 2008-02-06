package tud.gamecontroller.game.javaprover;

import java.util.AbstractList;
import java.util.List;

import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.Connective;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Expression;
import cs227b.teamIago.resolver.Variable;

import tud.gamecontroller.game.AbstractTerm;
import tud.gamecontroller.game.TermInterface;

public class Term extends AbstractTerm{
	
	private Expression expr;
	
	public Term(Expression expr){
		this.expr=expr;
	}

	public String getName() {
		return expr.firstOp().toString();
	}

	public Expression getExpr(){
		return expr;
	}
	
	public boolean isConstant() {
		return expr instanceof Atom;
	}

	public boolean isVariable() {
		return expr instanceof Variable;
	}
	
	public boolean isGround() {
		return expr.getVars().size()>0;
	}

	public List<TermInterface> getArgs() {
		if(expr instanceof Connective){
			return new TermList(((Connective)expr).getOperands());
		}else{
			return new TermList(new ExpList());
		}
	}
	
	private class TermList extends AbstractList<TermInterface>{
		private ExpList expList;

		public TermList(ExpList l){
			this.expList=l;
		}
		
		public TermInterface get(int index) {
			return new Term(expList.get(index));
		}

		public int size() {
			return expList.size();
		}
		
	}

}
