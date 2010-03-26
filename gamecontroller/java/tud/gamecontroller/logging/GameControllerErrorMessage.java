/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de>, Stephan Schiffel <stephan.schiffel@gmx.de>

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

package tud.gamecontroller.logging;


public class GameControllerErrorMessage {
	public static final String TIMEOUT = "timeout";
	public static final String ILLEGAL_MOVE = "illegal_move";
	public static final String PARSING_ERROR = "parsing_error";
	public static final String UNKNOWN_HOST = "unknown_host";
	public static final String IO_ERROR = "io_error";
	public static final String GAME_ERROR = "game_error";
	public static final String NOREADY = "no \"ready\" ack from HumanPlayer";
	public static final String ABORTED = "aborted";
	public static final String PLAYER_DISABLED = "player_disabled";
	public static final String INTERNAL_ERROR = "internal";
	
	private static final int MAX_TYPE_LENGTH = 40;
	private static final int MAX_MESSAGE_LENGTH = 255;

	private String type;
	private String message;
	private String playerName = null;
	
	public GameControllerErrorMessage(String type) {
		this(type, "");
	}
	
	public GameControllerErrorMessage(String type, String message) {
		if (type == null) {
			throw new IllegalArgumentException("type must not be null!");
		}
		this.type = type;
		this.message = message;
	}

	public GameControllerErrorMessage(String type, String message, String playerName) {
		this(type, message);
		this.playerName = playerName;
	}

	/**
	 * must not return null. maximum length 40.
	 */
	public String getType() {
		assert(type != null);
		
		if (type.length() > MAX_TYPE_LENGTH) {
			return type.substring(0, MAX_TYPE_LENGTH);
		}
		else {
			return type;
		}
	}
	
	/**
	 * must not return null. maximum length 255.
	 */
	public String getMessage() {
		if (message == null) {
			return "";
		}
		else if (message.length() > MAX_MESSAGE_LENGTH) {
			return message.substring(0, MAX_MESSAGE_LENGTH);
		}
		else {
			return message;
		}
	}

	public String getPlayerName() {
		return playerName;
	}

	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append("[GameControllerErrorMessage:");
		buffer.append(" type: ");
		buffer.append(type);
		buffer.append(" message: ");
		buffer.append(message);
		buffer.append(" playerName: ");
		buffer.append(playerName);
		buffer.append("]");
		return buffer.toString();
	}
}
