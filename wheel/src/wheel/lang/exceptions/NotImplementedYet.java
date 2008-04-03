package wheel.lang.exceptions;

public class NotImplementedYet extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public NotImplementedYet() {
		super("Not implemented yet :)");
	}

	public NotImplementedYet(Throwable unhandledThrowable) {
		super("Exception not handled yet.", unhandledThrowable);
	}
	
}
