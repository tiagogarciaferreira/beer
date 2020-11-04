package com.tgfcodes.beer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.tgfcodes.beer.model.Venda;
import com.tgfcodes.beer.repository.helper.venda.VendasQueries;

@Repository
@Transactional
public interface VendaRepository extends JpaRepository<Venda, Long>, VendasQueries{

}
