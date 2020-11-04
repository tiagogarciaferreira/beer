package com.tgfcodes.beer.service.exception;

public class NomeEstiloJaExisteException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public NomeEstiloJaExisteException(String message) {
		super(message);
	}
	
}
