package com.seabed.ohm.exceptions;

public class NoIDFieldFoundException extends SeabedOHMException {

	private static final long serialVersionUID = 1L;

	public NoIDFieldFoundException() {
		super("No ID field found in this class.");
	}

}
