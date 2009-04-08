package tud.ggpserver.scheduler;

import java.util.logging.Handler;
import java.util.logging.LogRecord;


import tud.gamecontroller.logging.GameControllerErrorMessage;
import tud.ggpserver.datamodel.Match;

public class LoggingHandler extends Handler {
	private final AbstractRoundRobinScheduler scheduler;
	
	public LoggingHandler(AbstractRoundRobinScheduler scheduler) {
		this.scheduler = scheduler;
	}

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
			
			Match currentMatch = scheduler.getCurrentMatch();
			
			if (currentMatch != null) {
				currentMatch.updateErrorMessage(message);
			}
		}
	}

}
