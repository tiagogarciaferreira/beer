package com.tgfcodes.beer.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tgfcodes.beer.controller.wrapper.PageWrapper;
import com.tgfcodes.beer.model.Estilo;
import com.tgfcodes.beer.repository.filter.EstiloFilter;
import com.tgfcodes.beer.service.EstiloService;
import com.tgfcodes.beer.service.exception.ImpossivelExcluirEntidadeException;
import com.tgfcodes.beer.service.exception.NomeEstiloJaExisteException;

@Controller
@RequestMapping(value = "/estilo")
public class EstilosController {
	
	@Autowired
	private EstiloService estiloService;
	
	@GetMapping(value = "/novo")
	private ModelAndView novo(Estilo estilo) {
		ModelAndView modelAndView = new ModelAndView("/estilo/CadastroEstilo");
		modelAndView.addObject("estilo", estilo);
		return modelAndView;
	}
	
	@PostMapping(value = "/novo")
	private ModelAndView salvar(@Valid Estilo estilo, Errors errors, RedirectAttributes redirectAttributes) {
		if(errors.hasErrors()) {
			return this.novo(estilo);
		}
		try {
			this.estiloService.salvar(estilo);
		} catch (NomeEstiloJaExisteException ex) {
			errors.rejectValue("nome", ex.getMessage(), ex.getMessage());
			return this.novo(estilo);
		}
		redirectAttributes.addFlashAttribute("mensagem", "Estilo salvo com sucesso!");
		return new ModelAndView("redirect:/estilo/novo");
	}
	
	@ResponseBody//ajax-salvar
	@PostMapping(value = "/novo", consumes = { MediaType.APPLICATION_JSON_VALUE })
	private ResponseEntity<?> salvar(@RequestBody @Valid Estilo estilo, Errors errors) {
		if (errors.hasErrors()) {
			return ResponseEntity.badRequest().body(errors.getFieldError("nome").getDefaultMessage());
		}
		estilo = this.estiloService.salvar(estilo);
		return ResponseEntity.ok(estilo);
	}
	
	@GetMapping(value = "/pesquisar")
	private ModelAndView pesquisar(EstiloFilter estiloFilter, BindingResult bindingResult, Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView modelAndView = new ModelAndView("/estilo/PesquisaEstilo");
		PageWrapper<Estilo> pagina = new PageWrapper<>(this.estiloService.pesquisar(estiloFilter, pageable), httpServletRequest);
		modelAndView.addObject("pagina", pagina);
		return modelAndView;
	}
	
	@GetMapping("/editar/{codigo}")
	private ModelAndView editar(@PathVariable("codigo") Long codigo) {
		Estilo estilo = this.estiloService.findOne(codigo).get();
		ModelAndView modelAndView = this.novo(estilo);
		return modelAndView;
	}

	@ResponseBody
	@DeleteMapping("/remover/{codigo}")
	private ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		try {
			this.estiloService.excluir(codigo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	
}