package com.tgfcodes.beer.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.tgfcodes.beer.controller.wrapper.PageWrapper;
import com.tgfcodes.beer.model.Cliente;
import com.tgfcodes.beer.model.enumeration.TipoPessoa;
import com.tgfcodes.beer.repository.filter.ClienteFilter;
import com.tgfcodes.beer.service.ClienteService;
import com.tgfcodes.beer.service.EstadoService;
import com.tgfcodes.beer.service.exception.CpfOuCnpjJaExisteException;
import com.tgfcodes.beer.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping(value = "/cliente")
public class ClientesController {

	@Autowired
	private EstadoService estadoService;
	
	@Autowired
	private ClienteService clienteService;
	
	
	@GetMapping(value = "/novo")
	private ModelAndView novo(Cliente cliente) {
		ModelAndView modelAndView = new ModelAndView("/cliente/CadastroCliente");
		modelAndView.addObject("tiposPessoa", TipoPessoa.values());
		modelAndView.addObject("cliente", cliente);
		modelAndView.addObject("estados", this.estadoService.listar());
		return modelAndView;
	}
	
	@PostMapping(value = "/novo")
	private ModelAndView salvar(@Valid Cliente cliente, Errors errors, RedirectAttributes redirectAttributes) {
		if(errors.hasErrors()) {
			return this.novo(cliente);
		}
		try {
			this.clienteService.salvar(cliente);
		} catch (CpfOuCnpjJaExisteException e) {
			errors.rejectValue("cpfOuCnpj", e.getMessage(), e.getMessage());
			return this.novo(cliente);
		}
		redirectAttributes.addFlashAttribute("mensagem", "Cliente cadastrado com sucesso!");
		return new ModelAndView("redirect:/cliente/novo");
	}
	
	@GetMapping(value = "/pesquisar")
	private ModelAndView pesquisar(ClienteFilter clienteFilter, BindingResult bindingResult, Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView modelAndView = new ModelAndView("/cliente/PesquisaCliente");
		PageWrapper<Cliente> pagina = new PageWrapper<>(this.clienteService.pesquisar(clienteFilter, pageable), httpServletRequest);
		modelAndView.addObject("pagina", pagina);
		return modelAndView;
	}
	
	@ResponseBody
	@GetMapping(value = "/pesquisar", consumes = { MediaType.APPLICATION_JSON_VALUE })
	private List<Cliente> pesquisar(String nome) {
		this.validarTamanhoNome(nome);
		return this.clienteService.buscarPorNome(nome);
	}
	
	@GetMapping("/editar/{codigo}")
	private ModelAndView editar(@PathVariable("codigo") Long codigo) {
		Cliente cliente = this.clienteService.findOne(codigo).get();
		this.clienteService.comporDadosEndereco(cliente);
		ModelAndView modelAndView = this.novo(cliente);
		return modelAndView;
	}
	
	@ResponseBody
	@DeleteMapping("/remover/{codigo}")
	private ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		try {
			this.clienteService.excluir(codigo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}

	private void validarTamanhoNome(String nome) {
		if (StringUtils.isEmpty(nome) || nome.length() < 3) {
			throw new IllegalArgumentException();
		}
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	private ResponseEntity<Void> tratarIllegalArgumentException(IllegalArgumentException e) {
		return ResponseEntity.badRequest().build();
	}
	
}
