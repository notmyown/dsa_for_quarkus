package de.nmo.dsa.roller.error;

public class GenericException extends Exception {

	private static final long serialVersionUID = 7379659822768179370L;

	public GenericException() {
		super();
	}

	public GenericException(String message, Exception ex) {
		super(message, ex);
	}

	public GenericException(String message) {
		super(message);
	}

}
