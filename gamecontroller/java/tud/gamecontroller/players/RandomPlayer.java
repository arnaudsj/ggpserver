/*
    Copyright (C) 2008-2010 Stephan Schiffel <stephan.schiffel@gmx.de>
                  2010 Nicolas JEAN <njean42@gmail.com>

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
import java.util.Random;

import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.game.MoveInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.term.TermInterface;

public class RandomPlayer<
	TermType extends TermInterface,
	StateType extends StateInterface<TermType, ? extends StateType>> extends LocalPlayer<TermType, StateType>  {

	private Random random;
	
	public RandomPlayer(String name, GDLVersion gdlVersion) {
		super(name, gdlVersion);
		random=new Random();
	}
	
	public MoveInterface<TermType> getNextMove() {
		// does the work of getting either legal moves the GDL-I way, or the GDL-II way
		ArrayList<MoveInterface<TermType>> legalMoves = new ArrayList<MoveInterface<TermType>>(getLegalMoves());
		int i=random.nextInt(legalMoves.size());
		return legalMoves.get(i);
	}
}
