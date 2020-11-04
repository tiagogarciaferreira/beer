package com.tgfcodes.beer.repository.helper.estilo;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.tgfcodes.beer.model.Estilo;
import com.tgfcodes.beer.repository.filter.EstiloFilter;

public interface EstiloQueries {

	public Page<Estilo> filtrar(EstiloFilter filtro, Pageable pageable);
}
