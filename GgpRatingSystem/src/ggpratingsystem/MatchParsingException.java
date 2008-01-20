package ggpratingsystem;

/**
 * This Exception is thrown if anything goes wrong while parsing a Match XML file.
 * 
 * @author martin
 *
 */
public class MatchParsingException extends Exception {

	private static final long serialVersionUID = -5683610441304293710L;

	public MatchParsingException() {
		super();
	}

	public MatchParsingException(String message, Throwable cause) {
		super(message, cause);
	}

	public MatchParsingException(String message) {
		super(message);
	}

	public MatchParsingException(Throwable cause) {
		super(cause);
	}
}
