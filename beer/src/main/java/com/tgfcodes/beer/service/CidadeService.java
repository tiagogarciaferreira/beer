package com.tgfcodes.beer.service;

import java.util.List;
import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.tgfcodes.beer.model.Cidade;
import com.tgfcodes.beer.repository.CidadeRepository;
import com.tgfcodes.beer.repository.filter.CidadeFilter;
import com.tgfcodes.beer.service.exception.ImpossivelExcluirEntidadeException;
import com.tgfcodes.beer.service.exception.NomeCidadeJaExisteException;

@Service
public class CidadeService {

	@Autowired
	private CidadeRepository cidadeRepository;
	
	
	@Transactional(readOnly = false)
	public void salvar(Cidade cidade) {
		Optional<Cidade> cidadeExistente = this.cidadeRepository.findByNomeAndEstado(cidade.getNome(), cidade.getEstado());
		if(cidadeExistente.isPresent()) {
			throw new NomeCidadeJaExisteException("Nome de cidade já cadastrado.");
		}
		this.cidadeRepository.save(cidade);
	}
	
	@Transactional(readOnly = true)
	public List<Cidade> pesquisarPorEstado(Long codigoEstado){
		return this.cidadeRepository.findByEstadoCodigo(codigoEstado);
	}
	
	@Transactional(readOnly = true)
	public Page<Cidade> pesquisar(CidadeFilter cidadeFilter, Pageable pageable){
		return this.cidadeRepository.filtrar(cidadeFilter, pageable);
	}
	
	@Transactional(readOnly = false)
	public void excluir(Long codigo) {
		try {
			Cidade cidade = findOne(codigo).get();
			this.cidadeRepository.delete(cidade);
			this.cidadeRepository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar cidade. O registro está sendo usado.");
		}
	}

	@Transactional(readOnly = true)
	public Optional<Cidade> findOne(Long codigo) {
		return cidadeRepository.findById(codigo);
	}
	
}
