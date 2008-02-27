package tud.gamecontroller.game.jocular;

import java.util.AbstractList;
import java.util.List;

import stanfordlogic.gdl.SymbolTable;
import stanfordlogic.prover.TermFunction;
import stanfordlogic.prover.TermObject;
import stanfordlogic.prover.TermVariable;
import tud.gamecontroller.term.AbstractTerm;
import tud.gamecontroller.term.TermInterface;

public class Term extends AbstractTerm {
	private stanfordlogic.prover.Term stanfordlogicTerm;
	private SymbolTable symbolTable;
	
	public Term(SymbolTable symbolTable, stanfordlogic.prover.Term expr){
		this.symbolTable=symbolTable;
		this.stanfordlogicTerm=expr;
	}

	public String getName() {
		if(stanfordlogicTerm instanceof TermObject){
			return symbolTable.get(((TermObject)stanfordlogicTerm).getToken());
		}else if(stanfordlogicTerm instanceof TermFunction){
			return symbolTable.get(((TermFunction)stanfordlogicTerm).getName());
		}else if(stanfordlogicTerm instanceof TermVariable){
			return symbolTable.get(((TermVariable)stanfordlogicTerm).getName());
		}else{
			return null;
		}
	}

	public stanfordlogic.prover.Term getExpr(){
		return stanfordlogicTerm;
	}
	
	public boolean isConstant() {
		return stanfordlogicTerm instanceof stanfordlogic.prover.TermObject;
	}

	public boolean isVariable() {
		return stanfordlogicTerm instanceof stanfordlogic.prover.TermVariable;
	}
	
	public boolean isGround() {
		return !stanfordlogicTerm.hasVariables();
	}

	public List<TermInterface> getArgs() {
		if(stanfordlogicTerm instanceof stanfordlogic.prover.TermFunction){
			return new TermList((stanfordlogic.prover.TermFunction)stanfordlogicTerm);
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
			return stanfordlogicTerm.toString(symbolTable);
		}catch(Exception ex){
			return null;
		}
	}
}
