package com.reza.atm.exception;

public class CustomerNotFoundException extends AtmException {

	public CustomerNotFoundException() {
		super("Customer does not exist");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5955452206417826279L;

}
