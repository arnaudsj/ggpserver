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

import tud.gamecontroller.aux.InvalidKIFException;
import cs227b.teamIago.parser.Parser;
import cs227b.teamIago.resolver.Connective;
import cs227b.teamIago.resolver.ExpList;

public class TermFactory implements tud.gamecontroller.term.TermFactoryInterface<Term> {

	public Term getTermFromKIF(String kif) throws InvalidKIFException {
		Term term=null;
		try{
			ExpList list=Parser.parseDesc("(bla "+kif+")");
			if(list.size()>0){
				ExpList list2=((Connective)list.get(0)).getOperands();
				term=new Term(list2.get(0));
			}
		}catch(Exception ex){
			throw new InvalidKIFException("Exception while parsing \""+kif+"\":"+ex.getMessage());
		}
		if(term==null){
			throw new InvalidKIFException("not a valid kif term:"+kif);
		}
		return term;
	}

}
