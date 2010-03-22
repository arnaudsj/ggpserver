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

package tud.gamecontroller.gui;

import java.io.File;

import tud.gamecontroller.game.javaprover.ReasonerFactory;

public class GameControllerGuiRunnerFactory {

	public static AbstractGameControllerGuiRunner<?, ?> createGameControllerGuiRunner(File gameFile){
		return new tud.gamecontroller.game.javaprover.GameControllerGuiRunner(gameFile, new ReasonerFactory());
	}
}
