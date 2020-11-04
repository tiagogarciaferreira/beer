package com.tgfcodes.beer.service.exception;

public class SenhaObrigatoriaException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public SenhaObrigatoriaException(String message) {
		super(message);
	}
	
}
