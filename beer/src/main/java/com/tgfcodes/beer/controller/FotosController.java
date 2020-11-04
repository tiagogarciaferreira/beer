package com.tgfcodes.beer.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.async.DeferredResult;
import org.springframework.web.multipart.MultipartFile;

import com.tgfcodes.beer.dto.FotoDTO;
import com.tgfcodes.beer.storage.FotoStorage;
import com.tgfcodes.beer.storage.FotoStorageRunnable;

@RestController
@RequestMapping("/foto")
public class FotosController {
	
	@Autowired
	private FotoStorage fotoStorage;

	@PostMapping(value = "/upload")
	private DeferredResult<FotoDTO> upload(@RequestParam("files[]") MultipartFile[] files) {
		DeferredResult<FotoDTO> resultado = new DeferredResult<>();
		Thread thread = new Thread(new FotoStorageRunnable(files, resultado, fotoStorage));
		thread.start();
		return resultado;
	}
	
	@GetMapping(value = "/{nome:.*}")
	private byte[] recuperarFoto(@PathVariable String nome) {
		return fotoStorage.recuperar(nome);
	}
	
	
}