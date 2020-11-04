package com.tgfcodes.beer.controller;

import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.tgfcodes.beer.controller.validator.VendaValidator;
import com.tgfcodes.beer.controller.wrapper.PageWrapper;
import com.tgfcodes.beer.mail.Mailer;
import com.tgfcodes.beer.model.Cerveja;
import com.tgfcodes.beer.model.ItemVenda;
import com.tgfcodes.beer.model.Venda;
import com.tgfcodes.beer.model.enumeration.StatusVenda;
import com.tgfcodes.beer.model.enumeration.TipoPessoa;
import com.tgfcodes.beer.repository.filter.VendaFilter;
import com.tgfcodes.beer.security.UsuarioSistema;
import com.tgfcodes.beer.service.CervejaService;
import com.tgfcodes.beer.service.VendaService;
import com.tgfcodes.beer.session.TabelasItensSession;

@Controller
@RequestMapping(value = "/venda")
public class VendasController {
	
	@Autowired
	private TabelasItensSession tabelaItens;
	@Autowired
	private CervejaService cervejaService;
	@Autowired
	private VendaService vendaService;
	@Autowired
	private VendaValidator vendaValidator;
	@Autowired
	private Mailer mailer;
	
	@InitBinder(value = "venda")
	private void inicializarValidador(WebDataBinder binder) {
		binder.setValidator(vendaValidator);
	}

	@GetMapping(value = "/nova")
	private ModelAndView nova(Venda venda) {
		ModelAndView modelAndView = new ModelAndView("/venda/CadastroVenda");
		this.setUuid(venda);
		modelAndView.addObject("itens", venda.getItens());
		modelAndView.addObject("valorFrete", venda.getValorFrete());
		modelAndView.addObject("valorDesconto", venda.getValorDesconto());
		modelAndView.addObject("valorTotalItens", tabelaItens.getValorTotal(venda.getUuid()));
		modelAndView.addObject("venda", venda);
		return modelAndView;
	}
	
	@PostMapping(value = "/nova", params = "salvar")
	private ModelAndView salvar(Venda venda, Errors errors, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		this.validarVenda(venda, errors);
		if (errors.hasErrors()) {
			return this.nova(venda);
		}
		venda.setUsuario(usuarioSistema.getUsuario());
		this.vendaService.salvar(venda);
		attributes.addFlashAttribute("mensagem", "Venda salva com sucesso!");
		return new ModelAndView("redirect:/venda/nova");
	}
	
	@PostMapping(value = "/nova", params = "emitir")
	private ModelAndView emitir(Venda venda, Errors errors, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		this.validarVenda(venda, errors);
		if (errors.hasErrors()) {
			return this.nova(venda);
		}
		venda.setUsuario(usuarioSistema.getUsuario());
		this.vendaService.emitir(venda);
		attributes.addFlashAttribute("mensagem", "Venda emitida com sucesso!");
		return new ModelAndView("redirect:/venda/nova");
	}
	
	@PostMapping(value = "/nova", params = "enviarEmail")
	private ModelAndView enviarEmail(Venda venda, Errors errors, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		this.validarVenda(venda, errors);
		if (errors.hasErrors()) {
			return this.nova(venda);
		}
		venda.setUsuario(usuarioSistema.getUsuario());
		venda = this.vendaService.salvar(venda);
		this.mailer.enviar(venda);
		attributes.addFlashAttribute("mensagem", "Venda salva e e-mail enviado com sucesso!");
		return new ModelAndView("redirect:/venda/nova");
	}
	
	@PostMapping(value = "/item")
	private ModelAndView adicionarItem(Long codigoCerveja, String uuid) {
		Cerveja cerveja = this.cervejaService.findOne(codigoCerveja).get();
		this.tabelaItens.adicionarItem(uuid, cerveja, 1);
		return this.mvTabelaItensVenda(uuid);
	}
	
	@PutMapping(value = "/item/{codigoCerveja}")
	private ModelAndView alterarQuantidadeItem(@PathVariable Long codigoCerveja, Integer quantidade, String uuid) {
		Cerveja cerveja = this.cervejaService.findOne(codigoCerveja).get();
		this.tabelaItens.alterarQuantidadeItens(uuid, cerveja, quantidade);
		return this.mvTabelaItensVenda(uuid);
	}
	
	@DeleteMapping("/item/{uuid}/{codigoCerveja}")
	private ModelAndView excluirItem(@PathVariable Long codigoCerveja, @PathVariable String uuid) {
		Cerveja cerveja = this.cervejaService.findOne(codigoCerveja).get();
		this.tabelaItens.excluirItem(uuid, cerveja);
		return this.mvTabelaItensVenda(uuid);
	}
	
	@GetMapping(value = "/pesquisar")
	private ModelAndView pesquisar(VendaFilter vendaFilter, @PageableDefault(size = 3) Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView modelAndView = new ModelAndView("/venda/PesquisaVenda");
		modelAndView.addObject("todosStatus", StatusVenda.values());
		modelAndView.addObject("tiposPessoa", TipoPessoa.values());
		PageWrapper<Venda> paginaWrapper = new PageWrapper<>(this.vendaService.filtrar(vendaFilter, pageable), httpServletRequest);
		modelAndView.addObject("pagina", paginaWrapper);
		return modelAndView;
	}
	

	@GetMapping("/editar/{codigo}")
	private ModelAndView editar(@PathVariable("codigo") Long codigo) {
		Venda venda = this.vendaService.findOne(codigo).get();
		this.setUuid(venda);
		for (ItemVenda item : venda.getItens()) {
			this.tabelaItens.adicionarItem(venda.getUuid(), item.getCerveja(), item.getQuantidade());
		}
		ModelAndView modelAndView = this.nova(venda);
		return modelAndView;
	}
	
	@PostMapping(value = "/nova", params = "cancelar")
	private ModelAndView cancelar(Venda venda, Errors errors, RedirectAttributes attributes, @AuthenticationPrincipal UsuarioSistema usuarioSistema) {
		try {
			this.vendaService.cancelar(venda);
		} catch (AccessDeniedException e) {
			return new ModelAndView("/erro/403");
		}
		attributes.addFlashAttribute("mensagem", "Venda cancelada com sucesso!");
		return new ModelAndView("redirect:/venda/editar/" + venda.getCodigo());
	}
	
	private ModelAndView mvTabelaItensVenda(String uuid) {
		ModelAndView modelAndView = new ModelAndView("/venda/TabelaItensVenda");
		modelAndView.addObject("itens", this.tabelaItens.getItens(uuid));
		modelAndView.addObject("valorTotal", this.tabelaItens.getValorTotal(uuid));
		return modelAndView;
	}
	
	private void validarVenda(Venda venda, Errors errors) {
		venda.adicionarItens(this.tabelaItens.getItens(venda.getUuid()));
		venda.calcularValorTotal();
		this.vendaValidator.validate(venda, errors);
	}
	
	private void setUuid(Venda venda) {
		if (StringUtils.isEmpty(venda.getUuid())) {
			venda.setUuid(UUID.randomUUID().toString());
		}
	}
	
}
