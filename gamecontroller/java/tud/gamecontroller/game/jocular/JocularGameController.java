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

import stanfordlogic.prover.ProofContext;
import tud.gamecontroller.GameController;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.impl.State;

public class JocularGameController extends GameController<Term, ProofContext> {

	public JocularGameController(RunnableMatchInterface<Term, State<Term, ProofContext>> match) {
		super(match);
	}

}
