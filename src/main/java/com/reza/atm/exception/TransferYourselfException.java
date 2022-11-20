package com.reza.atm.exception;

public class TransferYourselfException extends AtmException {

	public TransferYourselfException() {
		super("Cannot transfer to yourself");
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 5955452206417826279L;

}
