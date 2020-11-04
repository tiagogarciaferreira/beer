package com.tgfcodes.beer.repository.helper.estilo;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.tgfcodes.beer.model.Estilo;
import com.tgfcodes.beer.repository.filter.EstiloFilter;
import com.tgfcodes.beer.repository.pagination.PaginacaoUtil;

public class EstiloRepositoryImpl implements EstiloQueries {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private PaginacaoUtil<Estilo> paginacaoutil;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Estilo> filtrar(EstiloFilter filtro, Pageable pageable) {
		TypedQuery<Estilo> typedQuery = (TypedQuery<Estilo>) adicionarFiltro(filtro, pageable.getSort());
		typedQuery = paginacaoutil.paginar(pageable, typedQuery);
		return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
	}
	
	@SuppressWarnings("unchecked")
	private Long total(EstiloFilter filtro) {
		TypedQuery<Long> quantidadeTypedQuery = (TypedQuery<Long>) adicionarFiltro(filtro, null);
		return quantidadeTypedQuery.getSingleResult().longValue();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TypedQuery adicionarFiltro(EstiloFilter filtro, Sort sort) {
		
     	CriteriaBuilder builder = entityManager.getCriteriaBuilder();
     	CriteriaQuery criteriaQuery = (sort == null) ? builder.createQuery(Long.class) : builder.createQuery(Estilo.class);
		Root<Estilo> estiloRoot = criteriaQuery.from(Estilo.class);
		criteriaQuery = (sort == null) ? criteriaQuery.select(builder.count(estiloRoot)) : criteriaQuery.select(estiloRoot);
		
		if (sort != null && !sort.isUnsorted()) {
			Sort.Order order = sort.iterator().next();
			String field = order.getProperty();
			criteriaQuery.orderBy(order.isAscending() ? builder.asc(estiloRoot.get(field)) : builder.desc(estiloRoot.get(field)));
		}

		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteriaQuery.where(builder.like(builder.lower(estiloRoot.get("nome")), "%"+filtro.getNome().toLowerCase()+"%"));
			}
		}
		return entityManager.createQuery(criteriaQuery);
	}
	
}