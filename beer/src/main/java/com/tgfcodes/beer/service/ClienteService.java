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
import com.tgfcodes.beer.model.Cliente;
import com.tgfcodes.beer.repository.CidadeRepository;
import com.tgfcodes.beer.repository.ClienteRepository;
import com.tgfcodes.beer.repository.filter.ClienteFilter;
import com.tgfcodes.beer.service.exception.CpfOuCnpjJaExisteException;
import com.tgfcodes.beer.service.exception.ImpossivelExcluirEntidadeException;

@Service
public class ClienteService {

	@Autowired
	private ClienteRepository clienteRepository;
	
	@Autowired
	private CidadeRepository cidadeRepository;
	
	
	@Transactional(readOnly = false)
	public void salvar(Cliente cliente) {
		Optional<Cliente> clienteExistente = this.clienteRepository.findByCpfOuCnpj(cliente.getCpfOuCnpjSemFormatacao());
		if (clienteExistente.isPresent()) {
			throw new CpfOuCnpjJaExisteException("CPF/CNPJ já cadastrado.");
		}
		this.clienteRepository.save(cliente);
	}
	
	@Transactional(readOnly = false)
	public void excluir(Long codigo) {
		try {
			Cliente cliente = this.findOne(codigo).get();
			this.clienteRepository.delete(cliente);
			this.clienteRepository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar cliente. Já está atrelado a alguma venda.");
		}
	}
	
	@Transactional(readOnly = true)
	public void comporDadosEndereco(Cliente cliente) {
		if (cliente.getEndereco() == null || cliente.getEndereco().getCidade() == null || cliente.getEndereco().getCidade().getCodigo() == null) {
			return;
		}
		Cidade cidade = this.cidadeRepository.findByCodigo(cliente.getEndereco().getCidade().getCodigo()).get();
		cliente.getEndereco().setCidade(cidade);
		cliente.getEndereco().setEstado(cidade.getEstado());
	}


	@Transactional(readOnly = true)
	public Page<Cliente> pesquisar(ClienteFilter clienteFilter, Pageable pageable) {
		return this.clienteRepository.filtrar(clienteFilter, pageable);
	}
	
	@Transactional(readOnly = true)
	public List<Cliente> buscarPorNome(String nome) {
		return this.clienteRepository.findByNomeStartingWithIgnoreCase(nome);
	}
	
	@Transactional(readOnly = true)
	public Optional<Cliente> findOne(Long codigo) {
		return this.clienteRepository.findById(codigo);
	}

	@Transactional(readOnly = true)
	public Long total() {
		return this.clienteRepository.count();
	}
	
}
