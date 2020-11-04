package com.tgfcodes.beer.controller;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.tgfcodes.beer.controller.wrapper.PageWrapper;
import com.tgfcodes.beer.model.Usuario;
import com.tgfcodes.beer.repository.StatusUsuario;
import com.tgfcodes.beer.repository.filter.UsuarioFilter;
import com.tgfcodes.beer.service.GrupoService;
import com.tgfcodes.beer.service.UsuarioService;
import com.tgfcodes.beer.service.exception.EmailJaExisteException;
import com.tgfcodes.beer.service.exception.ImpossivelExcluirEntidadeException;
import com.tgfcodes.beer.service.exception.SenhaObrigatoriaException;

@Controller
@RequestMapping(value = "/usuario")
public class UsuariosController {

	@Autowired
	private UsuarioService usuarioService;
	@Autowired
	private GrupoService grupoService;
	
	@GetMapping(value = "/novo")
	private ModelAndView novo(Usuario usuario) {
		ModelAndView modelAndView = new ModelAndView("usuario/CadastroUsuario");
		modelAndView.addObject("grupos", this.grupoService.listar());
		modelAndView.addObject("usuario", usuario);
		return modelAndView;
	}
	
	@PostMapping(value = "/novo")
	private ModelAndView salvar(@Valid Usuario usuario, Errors errors, RedirectAttributes redirectAttributes) {
		if(errors.hasErrors()) {
			return this.novo(usuario);
		}
		try {
			this.usuarioService.salvar(usuario);
		} catch (EmailJaExisteException e) {
			errors.rejectValue("email", e.getMessage(), e.getMessage());
			return this.novo(usuario);
		}catch (SenhaObrigatoriaException e) {
			errors.rejectValue("senha", e.getMessage(), e.getMessage());
			return this.novo(usuario);
		}
		
		redirectAttributes.addFlashAttribute("mensagem", "Usuário salvo com sucesso!");
		return new ModelAndView("redirect:/usuario/novo");
	}
	
	@PutMapping(value = "/status")
	@ResponseStatus(code = HttpStatus.OK)
	private void atualizarStatus(@RequestParam("codigos[]") Long[] codigos, @RequestParam("status") StatusUsuario statusUsuario) {
		this.usuarioService.alterarStatus(codigos, statusUsuario);
	}
	
	@GetMapping(value = "/pesquisar")
	private ModelAndView pesquisar(UsuarioFilter usuarioFilter, BindingResult bindingResult, Pageable pageable, HttpServletRequest httpServletRequest) {
		ModelAndView modelAndView = new ModelAndView("/usuario/PesquisaUsuario");
		PageWrapper<Usuario> pagina = new PageWrapper<>(this.usuarioService.pesquisar(usuarioFilter, pageable), httpServletRequest);
		modelAndView.addObject("pagina", pagina);
		modelAndView.addObject("grupos", this.grupoService.listar());
		return modelAndView;
	}
	
	@GetMapping("/editar/{codigo}")
	private ModelAndView editar(@PathVariable("codigo") Long codigo) {
		Usuario usuario = this.usuarioService.findOne(codigo).get();
		ModelAndView modelAndView = this.novo(usuario);
		return modelAndView;
	}
	
	@ResponseBody
	@DeleteMapping("/remover/{codigo}")
	private ResponseEntity<?> excluir(@PathVariable("codigo") Long codigo) {
		try {
			this.usuarioService.excluir(codigo);
		} catch (ImpossivelExcluirEntidadeException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		return ResponseEntity.ok().build();
	}
	
}
