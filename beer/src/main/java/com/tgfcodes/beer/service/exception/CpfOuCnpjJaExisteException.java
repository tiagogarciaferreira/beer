package com.tgfcodes.beer.service.exception;

public class CpfOuCnpjJaExisteException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public CpfOuCnpjJaExisteException(String message) {
		super(message);
	}
	
}
