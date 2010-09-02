package edu.frp.exception;

/**
 * @author Saulo (scsm@ecomp.poli.br)
 * 
 */
public class UnsupportedImageTypeException extends Exception {

	private static final long serialVersionUID = -5104731271601325693L;

	/**
	 * Constructor
	 */
	public UnsupportedImageTypeException() {
	}

	/**
	 * @param message
	 */
	public UnsupportedImageTypeException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public UnsupportedImageTypeException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public UnsupportedImageTypeException(String message, Throwable cause) {
		super(message, cause);
	}
}
