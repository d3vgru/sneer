package wheel.lang.exceptions;

/**
 * An exception which always contains an useful help
 * message to the end-user.
 */
public class FriendlyException extends Exception {

	private final String _help;
	
	public FriendlyException(String message, String help) {
		super(message);
		_help = help;
	}

	public FriendlyException(Throwable cause, String message, String help) {
		super(message, cause);
		_help = help;
	}


	public FriendlyException(Throwable cause, String help) {
		super(cause.getMessage(), cause);
		_help = help;
	}

	public String getHelp() {
		return _help;
	}

	private static final long serialVersionUID = 1L;
}
