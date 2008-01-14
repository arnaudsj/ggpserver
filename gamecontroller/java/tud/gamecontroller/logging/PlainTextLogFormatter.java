package tud.gamecontroller.logging;

import java.util.logging.LogRecord;
import java.util.logging.Formatter;

public class PlainTextLogFormatter extends Formatter {
	public synchronized String format(LogRecord record) {
		return record.getLevel().toString()+": "+record.getMessage();
	}
}
