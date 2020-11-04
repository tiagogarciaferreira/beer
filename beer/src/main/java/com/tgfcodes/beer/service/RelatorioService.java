package com.tgfcodes.beer.service;

import java.io.BufferedOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tgfcodes.beer.dto.VendaRelatorio;
import com.tgfcodes.beer.relatorio.ReportUtil;
import com.tgfcodes.beer.repository.VendaRepository;
import com.tgfcodes.beer.repository.filter.PeriodoRelatorioFilter;

@Service
public class RelatorioService {

	@Autowired
	private ReportUtil<VendaRelatorio> reportUtil;
	@Autowired
	private VendaRepository vendaRepository;
	
	public void gerarVendasEmitidas(PeriodoRelatorioFilter periodoRelatorioFilter, HttpServletResponse httpServletResponse) throws Exception {
		List<VendaRelatorio> listaRelatorio = vendaRepository.relatorioVendasEmitidas(periodoRelatorioFilter);
		byte[] relatorioPdf = reportUtil.gerarRelatorio(listaRelatorio, "relatorio_vendas_emitidas");
		String relatorioPDF = nomeRelatorio(periodoRelatorioFilter, ".pdf", "relatorio-vendas-emitidas-");
		httpServletResponse.setHeader("Content-Disposition", "attachment;filename=" + relatorioPDF);
		httpServletResponse.setContentLength(relatorioPdf.length);
		httpServletResponse.setContentType("application/octet-stream");
		BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(httpServletResponse.getOutputStream());
		bufferedOutputStream.write(relatorioPdf);
		bufferedOutputStream.flush();
	}
	
	private String nomeRelatorio(PeriodoRelatorioFilter periodoRelatorioFilter, String tipo, String nomeRelatorio) {
		StringBuilder nameRelatorioPdf = new StringBuilder();
		nameRelatorioPdf.append(nomeRelatorio);
		nameRelatorioPdf.append(LocalDate.parse(periodoRelatorioFilter.getDataInicio().toString()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		nameRelatorioPdf.append("-");
		nameRelatorioPdf.append(LocalDate.parse(periodoRelatorioFilter.getDataFim().toString()).format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
		nameRelatorioPdf.append(tipo);
		return nameRelatorioPdf.toString();
	}
	
}
