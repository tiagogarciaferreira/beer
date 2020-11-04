package com.tgfcodes.beer.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.tgfcodes.beer.model.Estilo;
import com.tgfcodes.beer.repository.helper.estilo.EstiloQueries;

@Repository
@Transactional
public interface EstiloRepository extends JpaRepository<Estilo, Long>, EstiloQueries {

	public Optional<Estilo> findByNomeIgnoreCase(String nome);
	
}
