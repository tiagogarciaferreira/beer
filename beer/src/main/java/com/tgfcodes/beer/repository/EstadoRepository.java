package com.tgfcodes.beer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tgfcodes.beer.model.Estado;

@Repository
@Transactional
public interface EstadoRepository extends JpaRepository<Estado, Long> {

}
