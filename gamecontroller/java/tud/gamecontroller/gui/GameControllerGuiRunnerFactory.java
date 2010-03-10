/*
    Copyright (C) 2008 Stephan Schiffel <stephan.schiffel@gmx.de>, Nicolas JEAN <njean42@gmail.com>

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

package tud.gamecontroller.gui;

import java.io.File;

import tud.gamecontroller.GDLVersion;
import tud.gamecontroller.ReasonerFactory;
import tud.gamecontroller.game.ReasonerInterface;
import tud.gamecontroller.game.javaprover.Reasoner;
import tud.gamecontroller.game.javaprover.Term;
import cs227b.teamIago.util.GameState;

public class GameControllerGuiRunnerFactory {

	public static AbstractGameControllerGuiRunner<?, ?> createGameControllerGuiRunner(File gameFile, GDLVersion gdlVersion){
		ReasonerFactory<Term, GameState> reasonerFactory = new ReasonerFactory<Term, GameState>() {
			public ReasonerInterface<Term, GameState> createReasoner(String gameDescription, String gameName) {
				return new Reasoner(gameDescription);
			}
		};
		
		return new tud.gamecontroller.game.javaprover.GameControllerGuiRunner(gameFile, reasonerFactory, gdlVersion);
	}
}
