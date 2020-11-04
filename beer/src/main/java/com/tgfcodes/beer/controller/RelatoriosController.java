package com.tgfcodes.beer.controller;

import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.tgfcodes.beer.repository.filter.PeriodoRelatorioFilter;
import com.tgfcodes.beer.service.RelatorioService;

@Controller
@RequestMapping(value = "/relatorio")
public class RelatoriosController {
	
	@Autowired
	private RelatorioService relatorioService; 
	
	@GetMapping(value = "/vendasEmitidas")
	private ModelAndView relatorioVendasEmitidas() {
		ModelAndView modelAndView = new ModelAndView("/relatorio/RelatorioVendasEmitidas");
		modelAndView.addObject(new PeriodoRelatorioFilter());
		return modelAndView;
	}
	
	@PostMapping(value = "/vendasEmitidas")
	private void gerarRelatorio(PeriodoRelatorioFilter periodoRelatorioFilter, HttpServletResponse httpServletResponse) throws Exception {
		relatorioService.gerarVendasEmitidas(periodoRelatorioFilter, httpServletResponse);
	}

}
