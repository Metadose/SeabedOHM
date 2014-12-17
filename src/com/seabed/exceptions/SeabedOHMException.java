package com.seabed.exceptions;

import com.seabed.util.TraceUtilities;

public class SeabedOHMException extends Exception {
	private static final long serialVersionUID = 1L;

	public SeabedOHMException() {
		super();
	}

	public SeabedOHMException(String message) {
		super(TraceUtilities.trace(message));
	}

	public SeabedOHMException(String message, Throwable cause) {
		super(TraceUtilities.trace(message), cause);
	}

	public SeabedOHMException(Throwable cause) {
		super(cause);
	}

}
