package tud.gamecontroller.logging;

import java.io.OutputStream;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;
import java.util.logging.StreamHandler;

public class UnbufferedStreamHandler extends StreamHandler {

	public UnbufferedStreamHandler() {
		super();
	}

	public UnbufferedStreamHandler(OutputStream out, Formatter formatter) {
		super(out, formatter);
	}

	public synchronized void publish(LogRecord record) {
		super.publish(record);
		super.flush();
	}

}
