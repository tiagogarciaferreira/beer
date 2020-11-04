package com.tgfcodes.beer.service.exception;

public class NomeCidadeJaExisteException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public NomeCidadeJaExisteException(String message) {
		super(message);
	}
	
}
