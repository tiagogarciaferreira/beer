package com.tgfcodes.beer.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.tgfcodes.beer.controller.wrapper.PageWrapper;
import com.tgfcodes.beer.model.Cidade;
import com.tgfcodes.beer.repository.filter.CidadeFilter;
import com.tgfcodes.beer.service.CidadeService;
import com.tgfcodes.beer.service.EstadoService;
import com.tgfcodes.beer.service.exception.ImpossivelExcluirEntidadeException;
import com.tgfcodes.beer.service.exception.NomeCidadeJaExisteException;

@Controller
@RequestMapping(value = "/cidade")
public class CidadesController {

	@Autowired
	private CidadeService cidadeService;
	@Autowired
	private EstadoService estadoService;
	
	@GetMapping(value = "/nova")
	private ModelAndView nova(Cidade cidade) {
		ModelAndView modelAndView = new ModelAndView("/cidade/CadastroCidade");
		modelAndView.addObject("cidade", cidade);
		modelAndView.addObject("estados", this.estadoService.listar());
		return modelAndView;
	}

	@PostMapping(value = "/nova")
	@CacheEvict(value = "cidades", key = "#cidade.estado.codigo", condition = "#cidade.temEstado()")
	private ModelAndView salvar(@Valid Cidade cidade, Errors errors, RedirectAttributes redirectAttributes) {
		if(errors.hasErrors()) {
			return this.nova(cidade);
		}
		try {
			this.cidadeService.salvar(cidade);
		} catch (NomeCidadeJaExisteException ex) {
			errors.rejectValue("nome", ex.getMessage(), ex.getMessage());
			return this.nova(cidade);
		}
		redirectAttributes.addFlashAttribute("mensagem", "Cidade salva com sucesso!");
		return new ModelAndView("redirect:/cidade/nova");
	}
	
	@Cacheable(value = "cidades", key = "#codigoEstado")
	@ResponseBody
	@RequestMapping(value = "/pesquisar", consumes = MediaType.APPLICATION_JSON_VALUE)
	private List<Cidade> pesquisarPorEstado(@RequestParam(name = "estado", defaultValue = "-1") Long codigoEstado) {
		return this.cidadeService.pesquisarPorEstado(codigoEstado);
	}
	
	@GetMapping(value = "/pesquisar")
	private ModelAndView pesquisar(CidadeFilter cidadeFilter, BindingResult bindingResult, Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView modelAndView = new ModelAndView("/cidade/PesquisaCidade");
		PageWrapper<Cidade> pagina = new PageWrapper<>(this.cidadeService.pesquisar(cidadeFilter, pageable), httpServletRequest);
		modelAndView.addObject("pagina", pagina);
		modelAndView.addObject("estados", estadoService.listar());
		return modelAndView;
	}
	
	@ResponseBody
	@DeleteMapping("/remover/{codigo}")
	private ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		try {
			this.cidadeService.excluir(codigo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	
	@GetMapping("/editar/{codigo}")
	private ModelAndView atualizar(@PathVariable("codigo") Long codigo) {
		Cidade cidade = this.cidadeService.findOne(codigo).get();
		ModelAndView modelAndView = this.nova(cidade);
		return modelAndView;
	}
	
	
}
