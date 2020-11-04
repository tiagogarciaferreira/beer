package com.tgfcodes.beer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tgfcodes.beer.model.Cidade;
import com.tgfcodes.beer.model.Estado;
import com.tgfcodes.beer.repository.helper.cidade.CidadeQueries;

@Repository
@Transactional
public interface CidadeRepository extends JpaRepository<Cidade, Long>, CidadeQueries  {

	public List<Cidade> findByEstadoCodigo(Long codigoEstado);
	public Optional<Cidade> findByNomeAndEstado(String nome, Estado estado);
	public Optional<Cidade> findByCodigo(Long codigo);
}
