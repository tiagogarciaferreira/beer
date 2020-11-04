package com.tgfcodes.beer.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.tgfcodes.beer.dto.VendaMes;
import com.tgfcodes.beer.dto.VendaOrigem;
import com.tgfcodes.beer.service.CervejaService;
import com.tgfcodes.beer.service.ClienteService;
import com.tgfcodes.beer.service.VendaService;

@Controller
@RequestMapping(value = "/dashboard")
public class DashboardController {

	@Autowired
	private VendaService vendaService;
	@Autowired
	private ClienteService clienteService;
	@Autowired
	private CervejaService cervejaService;
	
	@GetMapping(value = "/")
	private ModelAndView dashboard() {
		ModelAndView modelAndView = new ModelAndView("/home/Dashboard");
		modelAndView.addObject("vendasNoAno", this.vendaService.valorTotalNoAno());
		modelAndView.addObject("vendasNoMes", this.vendaService.valorTotalNoMes());
		modelAndView.addObject("ticketMedio", this.vendaService.valorTicketMedioNoAno());
		modelAndView.addObject("valorItensEstoque", this.cervejaService.valorItensEstoque());
		modelAndView.addObject("totalClientes", this.clienteService.total());
		return modelAndView;
	}
	
	@ResponseBody 
	@GetMapping("/totalPorMes")
	private List<VendaMes> listarTotalVendaPorMes() {
		return this.vendaService.totalPorMes();
	}
	
	@ResponseBody 
	@GetMapping("/porOrigem")
	private List<VendaOrigem> vendasPorNacionalidade() {
		return this.vendaService.totalPorOrigem();
	}
	
}