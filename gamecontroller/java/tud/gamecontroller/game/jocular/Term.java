/*
    Copyright (C) 2008 Stephan Schiffel <stephan.schiffel@gmx.de>

    This file is part of GameController.

    GameController is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GameController is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GameController.  If not, see <http://www.gnu.org/licenses/>.
*/

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
			return "";
		}
	}
}
