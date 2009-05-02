package tud.ggpserver.scheduler;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

import tud.gamecontroller.game.MatchInterface;
import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.ggpserver.datamodel.Match;

public class LoggingHandler extends Handler {
	@Override
	public void close() throws SecurityException {
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
