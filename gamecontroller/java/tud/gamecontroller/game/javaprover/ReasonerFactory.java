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

import tud.gamecontroller.ReasonerFactoryInterface;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.term.TermFactoryInterface;
import cs227b.teamIago.util.GameState;

public final class ReasonerFactory implements
		ReasonerFactoryInterface<Term, GameState> {
	public ReasonerInterface<Term, GameState> createReasoner(String gameDescription, String gameName) {
		return new Reasoner(gameDescription);
	}

	public TermFactoryInterface<Term> getTermFactory() {
		return new TermFactory();
	}
}