package tud.ggpserver.datamodel;

/**
 * This Exception is thrown whenever a new instance of an object (e.g., user) in
 * the MYSQL Database should be created whose primary key (e.g., user name)
 * already exists.
 * 
 * @author martin
 */
public class DuplicateInstanceException extends Exception {

	private static final long serialVersionUID = 385501759945166543L;

	public DuplicateInstanceException() {
	}

	public DuplicateInstanceException(String message) {
		super(message);
	}

	public DuplicateInstanceException(Throwable cause) {
		super(cause);
	}

	public DuplicateInstanceException(String message, Throwable cause) {
		super(message, cause);
	}

}
