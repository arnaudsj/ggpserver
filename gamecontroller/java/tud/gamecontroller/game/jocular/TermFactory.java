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

import stanfordlogic.gdl.GdlExpression;
import stanfordlogic.gdl.Parser;
import tud.gamecontroller.auxiliary.InvalidKIFException;

public class TermFactory implements tud.gamecontroller.term.TermFactoryInterface<Term> {
	Parser parser;

	public TermFactory(Parser parser){
		this.parser=parser;
	}
	
	public Term getTermFromKIF(String kif) throws InvalidKIFException {
		try{
			GdlExpression gdlExpr=parser.parse(kif).getElement(0);
			return new Term(parser.getSymbolTable(), stanfordlogic.prover.Term.buildFromGdl(gdlExpr));
		}catch(Exception ex){
			throw new InvalidKIFException("Exception while parsing \""+kif+"\":"+ex);
		}
	}
}
