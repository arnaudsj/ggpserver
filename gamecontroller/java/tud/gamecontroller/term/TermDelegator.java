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

package tud.gamecontroller.term;

import java.util.List;

public abstract class TermDelegator<TermType extends TermInterface> implements TermInterface {

	private TermType term;

	public TermDelegator(TermType term) {
		this.term=term;
	}
	
	public TermType getTerm(){
		return term;
	}

	public List<? extends TermInterface> getArgs() {
		return term.getArgs();
	}

	public String getKIFForm() {
		return term.getKIFForm();
	}

	public String getName() {
		return term.getName();
	}

	public String getPrefixForm() {
		return term.getPrefixForm();
	}

	public boolean isCompound() {
		return term.isCompound();
	}

	public boolean isConstant() {
		return term.isConstant();
	}

	public boolean isVariable() {
		return term.isVariable();
	}
	
	public boolean isGround() {
		return term.isGround();
	}
	
	public String toString() {
		return term.toString();
	}

	public boolean equals(Object obj) {
		if(obj instanceof TermDelegator<?>){
			return term.equals(((TermDelegator<?>)obj).getTerm());
		}else if(obj instanceof TermInterface){
			return term.equals(obj);
		}else{
			return false;
		}
	}

	public int hashCode() {
		return term.hashCode();
	}
}
