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

package tud.gamecontroller.game.impl;

import tud.gamecontroller.auxiliary.InvalidKIFException;
import tud.gamecontroller.game.MoveFactoryInterface;
import tud.gamecontroller.term.TermFactoryInterface;
import tud.gamecontroller.term.TermInterface;

public class MoveFactory<T extends TermInterface> implements MoveFactoryInterface<Move<T>> {
	
	private TermFactoryInterface<T> termFactory; 
	
	public MoveFactory(TermFactoryInterface<T> termFactory){
		this.termFactory=termFactory;
	}
	

	public Move<T> getMoveFromKIF(String kif) throws InvalidKIFException {
		Move<T> move=null;
		T t=termFactory.getTermFromKIF(kif);
		if(t!=null && !t.isGround()){
			throw new InvalidKIFException("\""+kif+"\" is not a ground term.");
		}else if(t!=null){
			move=new Move<T>(t);
		}
		return move;
	}
	
}
