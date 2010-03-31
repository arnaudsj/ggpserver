/*
    Copyright (C) 2010 Stephan Schiffel <stephan.schiffel@gmx.de>

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

import tud.gamecontroller.auxiliary.InvalidKIFException;
import cs227b.teamIago.parser.Parser;
import cs227b.teamIago.resolver.Connective;
import cs227b.teamIago.resolver.ExpList;
import cs227b.teamIago.resolver.Expression;

public class ParserAdapter {

	public static Expression parseExpression(String kif) throws InvalidKIFException {
		try{
			ExpList list=Parser.parseDesc("(bla "+kif+")");
			if(list.size() == 1){
				ExpList list2=((Connective)list.get(0)).getOperands();
				if(list2.size() == 1) {
					return list2.get(0);
				}
			}
		}catch(Exception ex){
			throw new InvalidKIFException("Exception while parsing \""+kif+"\":"+ex.getMessage());
		}
		throw new InvalidKIFException("not a valid kif term:"+kif);
	}

	public static ExpList parseExpressionList(String kif) throws InvalidKIFException {
		try{
			kif = kif.replace(")", ") ").trim();
			if(kif.charAt(0) != '(' || kif.charAt(kif.length()-1) != ')')
				throw new InvalidKIFException("not a valid kif list:"+kif);
			kif = "(bla " + kif.substring(1, kif.length()).trim();
			ExpList list=Parser.parseDesc(kif);
			if(list.size() != 1){
				throw new InvalidKIFException("Exception while parsing \""+kif+"\":...");
			}
			return ((Connective)list.get(0)).getOperands();
		}catch(Exception ex){
			throw new InvalidKIFException("Exception while parsing \""+kif+"\":"+ex.getMessage());
		}
	}
}
