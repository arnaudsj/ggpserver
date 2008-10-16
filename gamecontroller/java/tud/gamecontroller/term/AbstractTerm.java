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

public abstract class AbstractTerm<NativeTerm> implements TermInterface {

	private String kifForm=null;
	private String prefixForm=null;
	protected NativeTerm nativeTerm=null;
	
	public AbstractTerm(NativeTerm nativeTerm){
		this.nativeTerm=nativeTerm;
	}
	
	public NativeTerm getNativeTerm(){
		return nativeTerm;
	}
	
	public boolean isCompound() {
		return !isConstant() && !isVariable();
	}
	
	public String getKIFForm() {
		if(kifForm==null){
			StringBuilder s=new StringBuilder();
			if(!isCompound()){
				s.append(getName().toUpperCase());
			}else{
				s.append("(");
				s.append(getName().toUpperCase());
				for(TermInterface arg:getArgs()){
					s.append(" ");
					s.append(arg.getKIFForm());
				}
				s.append(")");
			}
			kifForm=s.toString();
		}
		return kifForm;
	}

	public String getPrefixForm() {
		if(prefixForm==null){
			StringBuilder s=new StringBuilder();
			if(!isCompound()){
				s.append(getName().toLowerCase());
			}else{
				s.append(getName().toLowerCase());
				s.append("(");
				boolean first=true;
				for(TermInterface arg:getArgs()){
					if(!first){
						s.append(",");
					}else{
						first=false;
					}
					s.append(arg.getPrefixForm());
				}
				s.append(")");
			}
			prefixForm=s.toString();
		}
		return prefixForm;
	}

	public boolean equals(Object obj) {
		if (obj instanceof TermInterface) {
			TermInterface t = (TermInterface) obj;
			return getKIFForm().equals(t.getKIFForm());
		}else{
			return false;
		}
	}

	public int hashCode() {
		return getKIFForm().hashCode();
	}

	public String toString(){
		return nativeTerm.toString();
	}

}
