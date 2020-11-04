package com.tgfcodes.beer.service.exception;

public class EmailJaExisteException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public EmailJaExisteException(String message) {
		super(message);
	}
	
}
