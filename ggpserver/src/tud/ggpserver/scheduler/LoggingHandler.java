/*
    Copyright (C) 2009 Martin Günther <mintar@gmx.de> 

    This file is part of GGP Server.

    GGP Server is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    GGP Server is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with GGP Server.  If not, see <http://www.gnu.org/licenses/>.
*/

package tud.ggpserver.scheduler;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.ggpserver.datamodel.Match;

public class LoggingHandler extends Handler {
	@Override
	public void close() {
		// nothing to do
	}

	@Override
	public void flush() {
		// nothing to do
	}

	@Override
	public void publish(LogRecord record) {
		Object[] parameters = record.getParameters();
		
		if ((parameters != null) 
				&& (parameters.length == 1) 
				&& (parameters[0] instanceof GameControllerErrorMessage)) {
			GameControllerErrorMessage message = (GameControllerErrorMessage) parameters[0];
			MatchInterface match = message.getMatch();
			
			if (match != null && match instanceof Match) {
				Match serverMatch = (Match) match; 
				serverMatch.updateErrorMessage(message);
			}
		}
	}
}
