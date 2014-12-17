package com.seabed.exceptions;

public class NoSBObjectAnnotationException extends SeabedOHMException {

	private static final long serialVersionUID = 1L;

	public NoSBObjectAnnotationException() {
		super("No SBObject annotation declared in this class.");
	}
}
