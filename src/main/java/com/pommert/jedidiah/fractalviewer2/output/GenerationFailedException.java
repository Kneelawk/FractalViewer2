package com.pommert.jedidiah.fractalviewer2.output;

public class GenerationFailedException extends RuntimeException {

	/**
	 * Generated serial id
	 */
	private static final long serialVersionUID = -3605136800421703542L;

	public GenerationFailedException() {
	}

	public GenerationFailedException(String message) {
		super(message);
	}

	public GenerationFailedException(Throwable cause) {
		super(cause);
	}

	public GenerationFailedException(String message, Throwable cause) {
		super(message, cause);
	}

	public GenerationFailedException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
