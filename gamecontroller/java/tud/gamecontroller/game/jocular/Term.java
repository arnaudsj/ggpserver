package tud.gamecontroller.game.jocular;

import java.util.AbstractList;
import java.util.List;

import stanfordlogic.gdl.SymbolTable;
import stanfordlogic.prover.TermFunction;
import stanfordlogic.prover.TermObject;
import stanfordlogic.prover.TermVariable;
import tud.gamecontroller.term.AbstractTerm;
import tud.gamecontroller.term.TermInterface;

public class Term extends AbstractTerm<stanfordlogic.prover.Term> {
	private SymbolTable symbolTable;
	
	public Term(SymbolTable symbolTable, stanfordlogic.prover.Term expr){
		super(expr);
		this.symbolTable=symbolTable;
	}

	public String getName() {
		if(nativeTerm instanceof TermObject){
			return symbolTable.get(((TermObject)nativeTerm).getToken());
		}else if(nativeTerm instanceof TermFunction){
			return symbolTable.get(((TermFunction)nativeTerm).getName());
		}else if(nativeTerm instanceof TermVariable){
			return symbolTable.get(((TermVariable)nativeTerm).getName());
		}else{
			return null;
		}
	}

	public stanfordlogic.prover.Term getExpr(){
		return nativeTerm;
	}
	
	public boolean isConstant() {
		return nativeTerm instanceof stanfordlogic.prover.TermObject;
	}

	public boolean isVariable() {
		return nativeTerm instanceof stanfordlogic.prover.TermVariable;
	}
	
	public boolean isGround() {
		return !nativeTerm.hasVariables();
	}

	public List<TermInterface> getArgs() {
		if(nativeTerm instanceof stanfordlogic.prover.TermFunction){
			return new TermList((stanfordlogic.prover.TermFunction)nativeTerm);
		}else{
			return new TermList(null);
		}
	}
	
	private class TermList extends AbstractList<TermInterface>{
		private TermFunction func;
		
		public TermList(TermFunction func) {
			this.func=func;
		}

		public TermInterface get(int index) {
			return (func==null?null:new Term(symbolTable, func.getTerm(index)));
		}

		public int size() {
			return (func==null?0:func.getArity());
		}
	}
	
	public String toString(){
		try{
			return nativeTerm.toString(symbolTable);
		}catch(Exception ex){
			return null;
		}
	}
}
