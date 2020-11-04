package com.tgfcodes.beer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tgfcodes.beer.model.Estado;
import com.tgfcodes.beer.repository.EstadoRepository;

@Service
public class EstadoService {

	@Autowired
	private EstadoRepository estadoRepository;
	
	@Transactional(readOnly = false)
	public void salvar(Estado estado) {
		estadoRepository.save(estado);
	}
	
	@Transactional(readOnly = true)
	public List<Estado> listar(){
		return estadoRepository.findAll();
	}
 	
}
