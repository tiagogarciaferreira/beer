package com.tgfcodes.beer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tgfcodes.beer.model.Cerveja;
import com.tgfcodes.beer.repository.helper.cerveja.CervejasQueries;

@Repository
@Transactional
public interface CervejaRepository extends JpaRepository<Cerveja, Long>, CervejasQueries {

}
