package com.tgfcodes.beer.repository.helper.cidade;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.tgfcodes.beer.model.Cidade;
import com.tgfcodes.beer.repository.filter.CidadeFilter;

public interface CidadeQueries {

	public Page<Cidade> filtrar(CidadeFilter filtro, Pageable pageable);
}
