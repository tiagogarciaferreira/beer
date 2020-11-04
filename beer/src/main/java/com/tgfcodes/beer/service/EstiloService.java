package com.tgfcodes.beer.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tgfcodes.beer.model.Estilo;
import com.tgfcodes.beer.repository.EstiloRepository;
import com.tgfcodes.beer.repository.filter.EstiloFilter;
import com.tgfcodes.beer.service.exception.ImpossivelExcluirEntidadeException;
import com.tgfcodes.beer.service.exception.NomeEstiloJaExisteException;

@Service
public class EstiloService {

	@Autowired
	private EstiloRepository estiloRepository;
	
	@Transactional(readOnly = false)
	public Estilo salvar(Estilo estilo) {
		Optional<Estilo> estiloExistente = estiloRepository.findByNomeIgnoreCase(estilo.getNome());
		if(estiloExistente.isPresent()) {
			throw new NomeEstiloJaExisteException("Nome de estilo já cadastrado.");
		}
		return this.estiloRepository.saveAndFlush(estilo);
	}
	
	@Transactional(readOnly = true)
	public List<Estilo> listar(){
		return this.estiloRepository.findAll();
	}
	
	@Transactional(readOnly = false)
	public void excluir(Long codigo) {
		try {
			Estilo estilo = findOne(codigo).get();
			this.estiloRepository.delete(estilo);
			this.estiloRepository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar estilo. Já está atrelado a alguma cerveja.");
		}
	}
	
	@Transactional(readOnly = true)
	public Page<Estilo> pesquisar(EstiloFilter estiloFilter, Pageable pageable){
		return this.estiloRepository.filtrar(estiloFilter, pageable);
	}
	
	@Transactional(readOnly = true)
	public Optional<Estilo> findOne(Long codigo){
		return this.estiloRepository.findById(codigo);
	}
	
}
