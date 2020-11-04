package com.tgfcodes.beer.controller.handler;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import com.tgfcodes.beer.service.exception.NomeEstiloJaExisteException;

@ControllerAdvice
public class ControllerAdviceExceptionHandler {

	@ExceptionHandler(NomeEstiloJaExisteException.class)
	public ResponseEntity<String> handleNomeEstiloJaCadastradoException(NomeEstiloJaExisteException e) {
		return ResponseEntity.badRequest().body(e.getMessage());
	}
}