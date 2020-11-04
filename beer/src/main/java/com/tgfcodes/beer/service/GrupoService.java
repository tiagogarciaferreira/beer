package com.tgfcodes.beer.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tgfcodes.beer.model.Grupo;
import com.tgfcodes.beer.repository.GrupoRepository;

@Service
public class GrupoService {
	
	@Autowired
	private GrupoRepository grupoRepository;
	
	@Transactional(readOnly = true)
	public List<Grupo> listar(){
		return this.grupoRepository.findAll();
	}

}
