package com.reza.atm.exception;

public class AmountBelowMinimumException extends AtmException {

	public AmountBelowMinimumException() {
		super("Minimum amount is 1");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5955452206417826279L;

}
