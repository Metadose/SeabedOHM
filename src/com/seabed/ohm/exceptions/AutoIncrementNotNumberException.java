package com.seabed.ohm.exceptions;

public class AutoIncrementNotNumberException extends SeabedOHMException {

	private static final long serialVersionUID = 1L;

	public AutoIncrementNotNumberException() {
		super("Auto-increment fields must be a number");
	}
}
