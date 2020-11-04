package com.tgfcodes.beer.repository.helper.cerveja;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tgfcodes.beer.dto.CervejaDTO;
import com.tgfcodes.beer.dto.ValorItensEstoque;
import com.tgfcodes.beer.model.Cerveja;
import com.tgfcodes.beer.repository.filter.CervejaFilter;

public interface CervejasQueries {

	public Page<Cerveja> filtrar(CervejaFilter filtro, Pageable pageable);
	public List<CervejaDTO> porSkuOuNome(String skuOuNome);
	public ValorItensEstoque valorItensEstoque();
}
