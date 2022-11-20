package com.reza.atm.exception;

public class AlreadyLoginException extends AtmException {

	public AlreadyLoginException() {
		super("Another customer already logged in");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5955452206417826279L;

}
