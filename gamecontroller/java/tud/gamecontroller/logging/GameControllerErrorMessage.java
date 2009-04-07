package tud.gamecontroller.logging;

public class GameControllerErrorMessage {
	public static final String TIMEOUT = "timeout";
	public static final String ILLEGAL_MOVE = "illegal_move";
	public static final String PARSING_ERROR = "parsing_error";
	public static final String UNKNOWN_HOST = "unknown_host";
	public static final String IO_ERROR = "io_error";
	public static final String ABORTED = "aborted";
	
	private static int MAX_TYPE_LENGTH = 40;
	private static int MAX_MESSAGE_LENGTH = 255;

	private String type;
	private String message;
	
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
}
