package com.tgfcodes.beer.repository.helper.usuario;

import java.util.List;
import java.util.Optional;
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
import com.tgfcodes.beer.model.Usuario;
import com.tgfcodes.beer.repository.filter.UsuarioFilter;
import com.tgfcodes.beer.repository.pagination.PaginacaoUtil;

public class UsuarioRepositoryImpl implements UsuariosQueries {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private PaginacaoUtil<Usuario> paginacaoutil;

	@Transactional(readOnly = true)
	@Override
	public Optional<Usuario> findByEmailAtivo(String email) {
		return entityManager
				.createQuery("from Usuario where lower(email) = lower(:email) and ativo = true", Usuario.class)
				.setParameter("email", email).getResultList().stream().findFirst();
	}

	@Transactional(readOnly = true)
	@Override
	public List<String> permissoes(Usuario usuario) {
		return entityManager.createQuery(
				"select distinct p.nome from Usuario u inner join u.grupos g inner join g.permissoes p where u = :usuario",
				String.class).setParameter("usuario", usuario).getResultList();
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Usuario> filtrar(UsuarioFilter filtro, Pageable pageable) {
		TypedQuery<Usuario> typedQuery = (TypedQuery<Usuario>) adicionarFiltro(filtro, pageable.getSort());
		typedQuery = paginacaoutil.paginar(pageable, typedQuery);
		return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
	}
	
	@SuppressWarnings("unchecked")
	private Long total(UsuarioFilter filtro) {
		TypedQuery<Long> quantidadeTypedQuery = (TypedQuery<Long>) adicionarFiltro(filtro, null);
		return quantidadeTypedQuery.getSingleResult().longValue();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TypedQuery adicionarFiltro(UsuarioFilter filtro, Sort sort) {
     	CriteriaBuilder builder = entityManager.getCriteriaBuilder();
     	CriteriaQuery criteriaQuery = (sort == null) ? builder.createQuery(Long.class) : builder.createQuery(Usuario.class);
		Root<Usuario> usuarioRoot = criteriaQuery.from(Usuario.class);
		criteriaQuery = (sort == null) ? criteriaQuery.select(builder.count(usuarioRoot)) : criteriaQuery.select(usuarioRoot);

		if (sort != null && !sort.isUnsorted()) {
			Sort.Order order = sort.iterator().next();
			String field = order.getProperty();
			criteriaQuery.orderBy(order.isAscending() ? builder.asc(usuarioRoot.get(field)) : builder.desc(usuarioRoot.get(field)));
		}

		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteriaQuery.where(builder.like(builder.lower(usuarioRoot.get("nome")),"%" + filtro.getNome().toLowerCase() + "%"));
			}
			if (!StringUtils.isEmpty(filtro.getEmail())) {
				criteriaQuery.where(builder.like(builder.lower(usuarioRoot.get("email")),"%" + filtro.getNome().toLowerCase() + "%"));
			}
			if(filtro.getGrupos() != null && !filtro.getGrupos().isEmpty()) {
				//codigo
			}
		}
		return entityManager.createQuery(criteriaQuery);
	}

}