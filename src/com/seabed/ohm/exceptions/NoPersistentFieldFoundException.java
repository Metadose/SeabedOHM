package com.seabed.ohm.exceptions;

public class NoPersistentFieldFoundException extends SeabedOHMException {

	private static final long serialVersionUID = 1L;

	public NoPersistentFieldFoundException() {
		super("No persistent field found in this class.");
	}
}
