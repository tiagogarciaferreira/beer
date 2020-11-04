package com.tgfcodes.beer.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.tgfcodes.beer.controller.wrapper.PageWrapper;
import com.tgfcodes.beer.dto.CervejaDTO;
import com.tgfcodes.beer.model.Cerveja;
import com.tgfcodes.beer.model.enumeration.Origem;
import com.tgfcodes.beer.model.enumeration.Sabor;
import com.tgfcodes.beer.repository.filter.CervejaFilter;
import com.tgfcodes.beer.service.CervejaService;
import com.tgfcodes.beer.service.EstiloService;
import com.tgfcodes.beer.service.exception.ImpossivelExcluirEntidadeException;

@Controller
@RequestMapping(value = "/cerveja")
public class CerveJasController {
	
	@Autowired
	private CervejaService cervejaService;
	@Autowired
	private EstiloService estiloService;
	
	@GetMapping(value = "/nova")
	private ModelAndView nova(Cerveja cerveja) {
		ModelAndView modelAndView = new ModelAndView("/cerveja/CadastroCerveja");
		modelAndView.addObject("cerveja", cerveja);
		modelAndView.addObject("sabores", Sabor.values());
		modelAndView.addObject("estilos", estiloService.listar());
		modelAndView.addObject("origens", Origem.values());
		return modelAndView;
	}
	
	@PostMapping(value = "/nova")
	private ModelAndView salvar(@Valid Cerveja cerveja, Errors errors, RedirectAttributes redirectAttributes) {
		if(errors.hasErrors()) {
			return nova(cerveja);
		}
		cervejaService.salvar(cerveja);
		redirectAttributes.addFlashAttribute("mensagem", "Cerveja cadastrada com sucesso!");
		return new ModelAndView("redirect:/cerveja/nova");
	}
	
	@ResponseBody
	@GetMapping(value = "/filtro", consumes = MediaType.APPLICATION_JSON_VALUE)
	private List<CervejaDTO> filtrar(String skuOuNome) {
		return cervejaService.porSkuOuNome(skuOuNome);
	}
	
	@GetMapping(value = "/pesquisar")
	private ModelAndView pesquisar(CervejaFilter cervejaFilter, BindingResult bindingResult, Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView modelAndView = new ModelAndView("/cerveja/PesquisaCerveja");
		modelAndView.addObject("estilos", estiloService.listar());
		modelAndView.addObject("sabores", Sabor.values());
		modelAndView.addObject("origens", Origem.values());
		PageWrapper<Cerveja> pagina = new PageWrapper<>(cervejaService.pesquisar(cervejaFilter, pageable), httpServletRequest);
		modelAndView.addObject("pagina", pagina);
		return modelAndView;
	}
	
	@ResponseBody 
	@DeleteMapping(value = "/remover/{codigo}")
	private ResponseEntity<?> remover(@PathVariable("codigo") Long codigo) {
		try {
			cervejaService.excluir(codigo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/editar/{codigo}")
	private ModelAndView editar(@PathVariable("codigo") Long codigo) {
		Cerveja cerveja = cervejaService.findOne(codigo).get();
		ModelAndView modelAndView = nova(cerveja);
		return modelAndView;
	}
	
}
