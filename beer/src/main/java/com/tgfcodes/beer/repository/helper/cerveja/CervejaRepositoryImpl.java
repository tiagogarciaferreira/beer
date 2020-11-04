package com.tgfcodes.beer.repository.helper.cerveja;

import java.util.List;

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

import com.tgfcodes.beer.dto.CervejaDTO;
import com.tgfcodes.beer.dto.ValorItensEstoque;
import com.tgfcodes.beer.model.Cerveja;
import com.tgfcodes.beer.repository.filter.CervejaFilter;
import com.tgfcodes.beer.repository.pagination.PaginacaoUtil;
import com.tgfcodes.beer.storage.FotoStorage;

public class CervejaRepositoryImpl implements CervejasQueries {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private PaginacaoUtil<Cerveja> paginacaoutil;
	
	@Autowired
	private FotoStorage fotoStorage;
	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Cerveja> filtrar(CervejaFilter filtro, Pageable pageable) {
		TypedQuery<Cerveja> typedQuery = (TypedQuery<Cerveja>) adicionarFiltro(filtro, pageable.getSort());
		typedQuery = paginacaoutil.paginar(pageable, typedQuery);
		return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
	}
	
	@SuppressWarnings("unchecked")
	private Long total(CervejaFilter filtro) {
		TypedQuery<Long> quantidadeTypedQuery = (TypedQuery<Long>) adicionarFiltro(filtro, null);
		return quantidadeTypedQuery.getSingleResult().longValue();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TypedQuery adicionarFiltro(CervejaFilter filtro, Sort sort) {
     	CriteriaBuilder builder = entityManager.getCriteriaBuilder();
     	CriteriaQuery criteriaQuery = (sort == null) ? builder.createQuery(Long.class) : builder.createQuery(Cerveja.class);
		Root<Cerveja> cervejaRoot = criteriaQuery.from(Cerveja.class);
		criteriaQuery = (sort == null) ? criteriaQuery.select(builder.count(cervejaRoot)) : criteriaQuery.select(cervejaRoot);
		
		if (sort != null && !sort.isUnsorted()) {
			Sort.Order order = sort.iterator().next();
			String field = order.getProperty();
			criteriaQuery.orderBy(order.isAscending() ? builder.asc(cervejaRoot.get(field)) : builder.desc(cervejaRoot.get(field)));
		}
		
		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getSku())) {
				criteriaQuery.where(builder.equal(builder.lower(cervejaRoot.get("sku")), filtro.getSku().toLowerCase()));
			}
			
			if (!StringUtils.isEmpty(filtro.getNome())) {
				criteriaQuery.where(builder.like(builder.lower(cervejaRoot.get("nome")), "%"+filtro.getNome().toLowerCase()+"%"));
			}

			if (isEstiloPresente(filtro)) {
				criteriaQuery.where(builder.equal(builder.lower(cervejaRoot.get("estilo")), filtro.getEstilo()));
			}

			if (filtro.getSabor() != null) {
				criteriaQuery.where(builder.equal(builder.lower(cervejaRoot.get("sabor")), filtro.getSabor()));
			}

			if (filtro.getOrigem() != null) {
				criteriaQuery.where(builder.equal(builder.lower(cervejaRoot.get("origem")), filtro.getOrigem()));
			}

			if (filtro.getValorDe() != null) {
				criteriaQuery.where(builder.ge(cervejaRoot.get("valor"), filtro.getValorDe()));
			}

			if (filtro.getValorAte() != null) {
				criteriaQuery.where(builder.le(cervejaRoot.get("valor"), filtro.getValorAte()));
			}
		}
		return entityManager.createQuery(criteriaQuery);
	}
	
	private boolean isEstiloPresente(CervejaFilter filtro) {
		return filtro.getEstilo() != null && filtro.getEstilo().getCodigo() != null;
	}
	
	@Transactional(readOnly = true)
	@Override
	public List<CervejaDTO> porSkuOuNome(String skuOuNome) {
		String jpql = "select new com.tgfcodes.beer.dto.CervejaDTO(codigo, sku, nome, origem, valor, foto) "
				+ "from Cerveja where lower(sku) like lower(:skuOuNome) or lower(nome) like lower(:skuOuNome)";
		List<CervejaDTO> cervejasFiltradas = entityManager.createQuery(jpql, CervejaDTO.class)
					.setParameter("skuOuNome", skuOuNome + "%")
					.getResultList();
		cervejasFiltradas.forEach(c -> c.setUrlThumbnailFoto(fotoStorage.getUrl(FotoStorage.THUMBNAIL_PREFIX + c.getFoto())));
		return cervejasFiltradas;
	}
	
	@Transactional(readOnly = true)
	@Override
	public ValorItensEstoque valorItensEstoque() {
		String query = "select new com.tgfcodes.beer.dto.ValorItensEstoque(sum(valor * quantidadeEstoque), sum(quantidadeEstoque)) from Cerveja";
		return entityManager.createQuery(query, ValorItensEstoque.class).getSingleResult();
	}

}