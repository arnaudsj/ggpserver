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

package tud.gamecontroller.players;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.term.TermInterface;

public class RandomPlayer<
	TermType extends TermInterface
	> extends LocalPlayer<TermType>  {

	private Random random;
	
	public RandomPlayer(String name){
		super(name);
		random=new Random();
	}
	
	public MoveInterface<TermType> getNextMove() {
		List<MoveInterface<TermType>> legalmoves=new ArrayList<MoveInterface<TermType>>(currentState.getLegalMoves(role));
		int i=random.nextInt(legalmoves.size());
		return legalmoves.get(i);
	}
}
