package tud.gamecontroller.game.javaprover;

import java.util.AbstractList;
import java.util.List;

import tud.gamecontroller.term.AbstractTerm;
import tud.gamecontroller.term.TermInterface;
import cs227b.teamIago.resolver.Atom;
import cs227b.teamIago.resolver.Connective;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Expression;
import cs227b.teamIago.resolver.Variable;

public class Term extends AbstractTerm<Expression>{
	
	public Term(Expression nativeTerm){
		super(nativeTerm);
	}

	public String getName() {
		return nativeTerm.firstOp().toString();
	}

	public Expression getExpr(){
		return nativeTerm;
	}
	
	public boolean isConstant() {
		return nativeTerm instanceof Atom;
	}

	public boolean isVariable() {
		return nativeTerm instanceof Variable;
	}
	
	public boolean isGround() {
		return nativeTerm.getVars().size()==0;
	}

	public List<TermInterface> getArgs() {
		if(nativeTerm instanceof Connective){
			return new TermList(((Connective)nativeTerm).getOperands());
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
