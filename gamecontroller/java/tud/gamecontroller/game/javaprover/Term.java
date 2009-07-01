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
	
	private static class TermList extends AbstractList<TermInterface>{
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
