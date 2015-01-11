package com.pommert.jedidiah.fractalviewer2.fractal;

public class FractalStartFailedException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5506485145788987544L;

	public FractalStartFailedException() {
		super();
	}

	public FractalStartFailedException(String message) {
		super(message);
	}

	public FractalStartFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public FractalStartFailedException(Throwable cause) {
		super(cause);
	}
}
