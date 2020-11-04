package com.tgfcodes.beer.repository.helper.cliente;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.tgfcodes.beer.model.Cliente;
import com.tgfcodes.beer.repository.filter.ClienteFilter;

public interface ClienteQueries {

	public Page<Cliente> filtrar(ClienteFilter filtro, Pageable pageable);
}
