package com.tgfcodes.beer.repository.helper.venda;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.MonthDay;
import java.time.Year;
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

import com.tgfcodes.beer.dto.VendaMes;
import com.tgfcodes.beer.dto.VendaOrigem;
import com.tgfcodes.beer.dto.VendaRelatorio;
import com.tgfcodes.beer.model.Venda;
import com.tgfcodes.beer.model.enumeration.StatusVenda;
import com.tgfcodes.beer.model.enumeration.TipoPessoa;
import com.tgfcodes.beer.repository.filter.PeriodoRelatorioFilter;
import com.tgfcodes.beer.repository.filter.VendaFilter;
import com.tgfcodes.beer.repository.pagination.PaginacaoUtil;

public class VendaRepositoryImpl implements VendasQueries {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private PaginacaoUtil<Venda> paginacaoutil;

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public Page<Venda> filtrar(VendaFilter filtro, Pageable pageable) {
		TypedQuery<Venda> typedQuery = (TypedQuery<Venda>) adicionarFiltro(filtro, pageable.getSort());
		typedQuery = paginacaoutil.paginar(pageable, typedQuery);
		return new PageImpl<>(typedQuery.getResultList(), pageable, total(filtro));
	}
	
	@SuppressWarnings("unchecked")
	private Long total(VendaFilter filtro) {
		TypedQuery<Long> quantidadeTypedQuery = (TypedQuery<Long>) adicionarFiltro(filtro, null);
		return quantidadeTypedQuery.getSingleResult().longValue();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private TypedQuery adicionarFiltro(VendaFilter filtro, Sort sort) {
     	CriteriaBuilder builder = entityManager.getCriteriaBuilder();
     	CriteriaQuery criteriaQuery = (sort == null) ? builder.createQuery(Long.class) : builder.createQuery(Venda.class);
		Root<Venda> vendaRoot = criteriaQuery.from(Venda.class);
		criteriaQuery = (sort == null) ? criteriaQuery.select(builder.count(vendaRoot)) : criteriaQuery.select(vendaRoot);
		
		if (sort != null && !sort.isUnsorted()) {
			Sort.Order order = sort.iterator().next();
			String field = order.getProperty();
			criteriaQuery.orderBy(order.isAscending() ? builder.asc(vendaRoot.get(field)) : builder.desc(vendaRoot.get(field)));
		}

		if (filtro != null) {
			if (!StringUtils.isEmpty(filtro.getCodigo())) {
				criteriaQuery.where(builder.equal(vendaRoot.get("codigo"), filtro.getCodigo()));
			}
			if (!StringUtils.isEmpty(filtro.getStatus())) {
				criteriaQuery.where(builder.equal(vendaRoot.get("status"), filtro.getStatus()));
			}
			if (filtro.getDesde() != null) {
				LocalDateTime desde = LocalDateTime.of(filtro.getDesde(), LocalTime.of(0, 0));
				criteriaQuery.where(builder.lessThanOrEqualTo(vendaRoot.get("dataCriacao").as(LocalDateTime.class), desde));
			}
			if (filtro.getAte() != null) {
				LocalDateTime ate = LocalDateTime.of(filtro.getAte(), LocalTime.of(23, 59));
				criteriaQuery.where(builder.greaterThanOrEqualTo(vendaRoot.get("dataCriacao").as(LocalDateTime.class), ate));
			}
			if (filtro.getValorMinimo() != null) {
				criteriaQuery.where(builder.ge(vendaRoot.get("valorTotal"), filtro.getValorMinimo()));
			}
			if (filtro.getValorMaximo() != null) {
				criteriaQuery.where(builder.le(vendaRoot.get("valorTotal"), filtro.getValorMaximo()));
			}
			if (!StringUtils.isEmpty(filtro.getNomeCliente())) {
				criteriaQuery.where(builder.like(builder.lower(vendaRoot.get("cliente.nome")), "%"+filtro.getNomeCliente().toLowerCase()+"%"));
			}
			if (!StringUtils.isEmpty(filtro.getCpfOuCnpjCliente())) {
				criteriaQuery.where(builder.equal(vendaRoot.get("cliente.cpfOuCnpj"), TipoPessoa.removerFormatacao(filtro.getCpfOuCnpjCliente())));
			}
			
		}
		return entityManager.createQuery(criteriaQuery);
	}
	
	@Override
	public BigDecimal valorTotalNoAno() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				entityManager.createQuery("select sum(valorTotal) from Venda where year(dataCriacao) = :ano and status = :status", BigDecimal.class)
					.setParameter("ano", Year.now().getValue())
					.setParameter("status", StatusVenda.EMITIDA)
					.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}
	
	@Override
	public BigDecimal valorTotalNoMes() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				entityManager.createQuery("select sum(valorTotal) from Venda where month(dataCriacao) = :mes and status = :status", BigDecimal.class)
					.setParameter("mes", MonthDay.now().getMonthValue())
					.setParameter("status", StatusVenda.EMITIDA)
					.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}
	
	@Override
	public BigDecimal valorTicketMedioNoAno() {
		Optional<BigDecimal> optional = Optional.ofNullable(
				entityManager.createQuery("select sum(valorTotal)/count(*) from Venda where year(dataCriacao) = :ano and status = :status", BigDecimal.class)
					.setParameter("ano", Year.now().getValue())
					.setParameter("status", StatusVenda.EMITIDA)
					.getSingleResult());
		return optional.orElse(BigDecimal.ZERO);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<VendaMes> totalPorMes() {
		List<VendaMes> vendasMes = entityManager.createNamedQuery("Vendas.totalPorMes").getResultList();
		LocalDate hoje = LocalDate.now();
		for (int i = 1; i <= 6; i++) {
			String mesIdeal = String.format("%d/%02d", hoje.getYear(), hoje.getMonthValue());
			boolean possuiMes = vendasMes.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
			if (!possuiMes) {
				vendasMes.add(i - 1, new VendaMes(mesIdeal, 0));
			}
			hoje = hoje.minusMonths(1);
		}
		return vendasMes;
	}
	
	@Override
	public List<VendaOrigem> totalPorOrigem() {
		List<VendaOrigem> vendasNacionalidade = entityManager.createNamedQuery("Vendas.porOrigem", VendaOrigem.class).getResultList();
		LocalDate now = LocalDate.now();
		for (int i = 1; i <= 6; i++) {
			String mesIdeal = String.format("%d/%02d", now.getYear(), now.getMonth().getValue());
			boolean possuiMes = vendasNacionalidade.stream().filter(v -> v.getMes().equals(mesIdeal)).findAny().isPresent();
			if (!possuiMes) {
				vendasNacionalidade.add(i - 1, new VendaOrigem(mesIdeal, 0, 0));
			}
			now = now.minusMonths(1);
		}
		return vendasNacionalidade;
	}

	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	@Override
	public List<VendaRelatorio> relatorioVendasEmitidas(PeriodoRelatorioFilter periodoRelatorioFilter) {
		List<VendaRelatorio> vendaRelatorio = entityManager.createNamedQuery("Vendas.relatorioEntreDatas")
				.setParameter("startDate", periodoRelatorioFilter.getDataInicio())
				.setParameter("endDate", periodoRelatorioFilter.getDataFim())
				.getResultList();
		return vendaRelatorio;
	}

}