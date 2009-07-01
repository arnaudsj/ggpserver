/*
Copyright (C) 2009 Martin Günther <mintar@gmx.de>

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

package tud.gamecontroller.exceptions;

/**
 * This class is unfortunately a RuntimeException, because getNextMove() in 
 * MovelistPlayer isn't allowed to throw a checked exception.
 * 
 * @author martin
 */
public class NoLegalMoveException extends RuntimeException {
	private static final long serialVersionUID = -8667653261242126576L;

	public NoLegalMoveException(Throwable cause) {
		super(cause);
	}

	public NoLegalMoveException(String message, Throwable cause) {
		super(message, cause);
	}

	public NoLegalMoveException(String message) {
		super(message);
	}

	public NoLegalMoveException() {
	}
}
