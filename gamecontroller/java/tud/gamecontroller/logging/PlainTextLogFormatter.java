package tud.gamecontroller.logging;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.LogRecord;
import java.util.logging.Formatter;

public class PlainTextLogFormatter extends Formatter {
	private static final DateFormat format = new SimpleDateFormat("HH:mm:ss.SSS");
	private static final String lineSep = System.getProperty("line.separator");
	
	public synchronized String format(LogRecord record) {
		return record.getLevel().getName()+"("+format.format(new Date(record.getMillis()))+")"+": "+record.getMessage()+lineSep;
	}
}
