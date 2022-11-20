package com.reza.atm.exception;

public class NotLoggedInException extends AtmException {

	public NotLoggedInException() {
		super("You need to login first");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5955452206417826279L;

}
