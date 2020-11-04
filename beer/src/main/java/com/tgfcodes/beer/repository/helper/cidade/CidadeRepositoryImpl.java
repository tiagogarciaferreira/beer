package com.tgfcodes.beer.repository.helper.cidade;

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
import com.tgfcodes.beer.model.Cidade;
import com.tgfcodes.beer.repository.filter.CidadeFilter;
import com.tgfcodes.beer.repository.pagination.PaginacaoUtil;

public class CidadeRepositoryImpl implements CidadeQueries {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private PaginacaoUtil<Cidade> paginacaoutil;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Cidade> filtrar(CidadeFilter filtro, Pageable pageable) {
		TypedQuery<Cidade> typedQuery = (TypedQuery<Cidade>) adicionarFiltro(filtro, pageable.getSort());
		typedQuery = paginacaoutil.paginar(pageable, typedQuery);
		return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
	}
	
	@SuppressWarnings("unchecked")
	private Long total(CidadeFilter filtro) {
		TypedQuery<Long> quantidadeTypedQuery = (TypedQuery<Long>) adicionarFiltro(filtro, null);
		return quantidadeTypedQuery.getSingleResult().longValue();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TypedQuery adicionarFiltro(CidadeFilter filtro, Sort sort) {
     	CriteriaBuilder builder = entityManager.getCriteriaBuilder();
     	CriteriaQuery criteriaQuery = (sort == null) ? builder.createQuery(Long.class) : builder.createQuery(Cidade.class);
		Root<Cidade> cidadeRoot = criteriaQuery.from(Cidade.class);
		criteriaQuery = (sort == null) ? criteriaQuery.select(builder.count(cidadeRoot)) : criteriaQuery.select(cidadeRoot);
		
		if (sort != null && !sort.isUnsorted()) {
			Sort.Order order = sort.iterator().next();
			String field = order.getProperty();
			criteriaQuery.orderBy(order.isAscending() ? builder.asc(cidadeRoot.get(field)) : builder.desc(cidadeRoot.get(field)));
		}

		if (filtro != null) {
			if (isEstadoPresente(filtro)) {
				criteriaQuery.where(builder.equal(cidadeRoot.get("estado"), filtro.getEstado()));
			}
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteriaQuery.where(builder.like(builder.lower(cidadeRoot.get("nome")), "%"+filtro.getNome().toLowerCase()+"%"));
			}
		}
		return entityManager.createQuery(criteriaQuery);
	}
	
	private boolean isEstadoPresente(CidadeFilter filtro) {
		return filtro.getEstado() != null && filtro.getEstado().getCodigo() != null;
	}
	
}