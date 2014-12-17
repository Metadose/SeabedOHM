package com.seabed.exceptions;

public class NoNamespaceFoundException extends SeabedOHMException {

	private static final long serialVersionUID = 1L;

	public NoNamespaceFoundException() {
		super("No namespace found in this class.");
	}
}
