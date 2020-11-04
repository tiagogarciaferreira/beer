package com.tgfcodes.beer.repository.helper.cliente;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Root;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.tgfcodes.beer.model.Cliente;
import com.tgfcodes.beer.repository.filter.ClienteFilter;
import com.tgfcodes.beer.repository.pagination.PaginacaoUtil;

public class ClienteRepositoryImpl implements ClienteQueries {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private PaginacaoUtil<Cliente> paginacaoutil;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Cliente> filtrar(ClienteFilter filtro, Pageable pageable) {
		TypedQuery<Cliente> typedQuery = (TypedQuery<Cliente>) adicionarFiltro(filtro, pageable.getSort());
		typedQuery = paginacaoutil.paginar(pageable, typedQuery);
		return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
	}
	
	@SuppressWarnings("unchecked")
	private Long total(ClienteFilter filtro) {
		TypedQuery<Long> quantidadeTypedQuery = (TypedQuery<Long>) adicionarFiltro(filtro, null);
		return quantidadeTypedQuery.getSingleResult().longValue();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TypedQuery adicionarFiltro(ClienteFilter filtro, Sort sort) {
     	CriteriaBuilder builder = entityManager.getCriteriaBuilder();
     	CriteriaQuery criteriaQuery = (sort == null) ? builder.createQuery(Long.class) : builder.createQuery(Cliente.class);
		Root<Cliente> clienteRoot = criteriaQuery.from(Cliente.class);
		criteriaQuery = (sort == null) ? criteriaQuery.select(builder.count(clienteRoot)) : criteriaQuery.select(clienteRoot);
		clienteRoot.fetch("endereco", JoinType.LEFT);
		
		if (sort != null && !sort.isUnsorted()) {
			Sort.Order order = sort.iterator().next();
			String field = order.getProperty();
			criteriaQuery.orderBy(order.isAscending() ? builder.asc(clienteRoot.get(field)) : builder.desc(clienteRoot.get(field)));
		}

		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteriaQuery.where(builder.like(builder.lower(clienteRoot.get("nome")), "%"+filtro.getNome().toLowerCase()+"%"));
			}
			if (!StringUtils.isEmpty(filtro.getCpfOuCnpj())) {
				criteriaQuery.where(builder.equal(clienteRoot.get("cpfOuCnpj"), filtro.getCpfOuCnpjSemFormatacao()));
			}
		}
		return entityManager.createQuery(criteriaQuery);
	}
	
}