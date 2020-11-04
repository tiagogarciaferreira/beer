package com.tgfcodes.beer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tgfcodes.beer.model.Cliente;
import com.tgfcodes.beer.repository.helper.cliente.ClienteQueries;

@Repository
@Transactional
public interface ClienteRepository extends JpaRepository<Cliente, Long>, ClienteQueries {

	
	public Optional<Cliente> findByCpfOuCnpj(String cpfOuCnpj);
	public List<Cliente> findByNomeStartingWithIgnoreCase(String nome);
	
}
