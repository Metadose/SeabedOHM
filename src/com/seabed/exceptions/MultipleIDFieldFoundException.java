package com.seabed.exceptions;

public class MultipleIDFieldFoundException extends SeabedOHMException {

	private static final long serialVersionUID = 1L;

	public MultipleIDFieldFoundException() {
		super("Multiple ID's defined in this class.");
	}
}
