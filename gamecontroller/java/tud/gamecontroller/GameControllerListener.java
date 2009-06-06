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

package tud.gamecontroller;

import java.util.Map;

import tud.gamecontroller.game.JointMoveInterface;
import tud.gamecontroller.game.RunnableMatchInterface;
import tud.gamecontroller.game.RoleInterface;
import tud.gamecontroller.game.StateInterface;
import tud.gamecontroller.term.TermInterface;

public interface GameControllerListener{

	void gameStarted(RunnableMatchInterface<? extends TermInterface, ?> match, StateInterface<? extends TermInterface, ?> currentState);

	void gameStep(JointMoveInterface<? extends TermInterface> jointmove, StateInterface<? extends TermInterface, ?> currentState);

	void gameStopped(StateInterface<? extends TermInterface, ?> currentState, Map<? extends RoleInterface<?>, Integer> goalValues);

}
