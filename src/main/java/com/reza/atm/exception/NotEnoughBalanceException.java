package com.reza.atm.exception;

public class NotEnoughBalanceException extends AtmException {

	public NotEnoughBalanceException() {
		super("Not enough balance");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5955452206417826279L;

}
