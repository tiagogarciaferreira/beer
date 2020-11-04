package com.tgfcodes.beer.repository.pagination;

import javax.persistence.TypedQuery;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class PaginacaoUtil<T> {
	
	public TypedQuery<T> paginar(Pageable pageable, TypedQuery<T> typedQuery) {
		int paginaAtual = pageable.getPageNumber();
		int totalRegistrosPorPagina = pageable.getPageSize();
		int primeiroRegistro = paginaAtual * totalRegistrosPorPagina;
		typedQuery.setFirstResult(primeiroRegistro);
		typedQuery.setMaxResults(totalRegistrosPorPagina);
		return typedQuery;
	}
}
