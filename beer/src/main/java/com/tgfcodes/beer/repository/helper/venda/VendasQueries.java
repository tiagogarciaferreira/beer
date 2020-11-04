package com.tgfcodes.beer.repository.helper.venda;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tgfcodes.beer.dto.VendaMes;
import com.tgfcodes.beer.dto.VendaOrigem;
import com.tgfcodes.beer.dto.VendaRelatorio;
import com.tgfcodes.beer.model.Venda;
import com.tgfcodes.beer.repository.filter.PeriodoRelatorioFilter;
import com.tgfcodes.beer.repository.filter.VendaFilter;

public interface VendasQueries {

	public Page<Venda> filtrar(VendaFilter filtro, Pageable pageable);
	public BigDecimal valorTotalNoAno();
	public BigDecimal valorTotalNoMes();
	public BigDecimal valorTicketMedioNoAno();
	public List<VendaMes> totalPorMes();
	public List<VendaOrigem> totalPorOrigem();
	public List<VendaRelatorio> relatorioVendasEmitidas(PeriodoRelatorioFilter periodoRelatorioFilter);
	
}
