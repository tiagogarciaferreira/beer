package com.tgfcodes.beer.service;

import java.util.Optional;

import javax.persistence.PersistenceException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import com.tgfcodes.beer.model.Usuario;
import com.tgfcodes.beer.repository.StatusUsuario;
import com.tgfcodes.beer.repository.UsuarioRepository;
import com.tgfcodes.beer.repository.filter.UsuarioFilter;
import com.tgfcodes.beer.service.exception.EmailJaExisteException;
import com.tgfcodes.beer.service.exception.ImpossivelExcluirEntidadeException;
import com.tgfcodes.beer.service.exception.SenhaObrigatoriaException;

@Service
public class UsuarioService {

	@Autowired
	private UsuarioRepository usuarioRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Transactional(readOnly = false)
	public void salvar(Usuario usuario) {
		Optional<Usuario> usuarioExistente = this.usuarioRepository.findByEmailIgnoreCase(usuario.getEmail());
		if(usuarioExistente.isPresent() && !usuarioExistente.get().equals(usuario)) {
			throw new EmailJaExisteException("E-mail já cadastrado.");
		}
		if(usuario.isNovo() && StringUtils.isEmpty(usuario.getSenha())) {
			throw new SenhaObrigatoriaException("Senha é obrigatória para novo usuário.");
		}
		if (usuario.isNovo() || !StringUtils.isEmpty(usuario.getSenha())) {
			usuario.setSenha(this.passwordEncoder.encode(usuario.getSenha()));
		} else if (StringUtils.isEmpty(usuario.getSenha())) {
			usuario.setSenha(usuarioExistente.get().getSenha());
		}
		usuario.setConfirmacaoSenha(usuario.getSenha());
		
		if (!usuario.isNovo() && usuario.getAtivo() == null) {
			usuario.setAtivo(usuarioExistente.get().getAtivo());
		}
		this.usuarioRepository.save(usuario);
	}
	
	@Transactional(readOnly = false)
	public void excluir(Long codigo) {
		try {
			Usuario usuario = findOne(codigo).get();
			this.usuarioRepository.delete(usuario);
			this.usuarioRepository.flush();
		} catch (PersistenceException e) {
			throw new ImpossivelExcluirEntidadeException("Impossível apagar usuário.");
		}
	}
	
	@Transactional(readOnly = false)
	public void alterarStatus(Long[] codigos, StatusUsuario statusUsuario) {
		statusUsuario.executar(codigos, usuarioRepository);
	}
	
	@Transactional(readOnly = true)
	public Page<Usuario> pesquisar(UsuarioFilter filtro, Pageable pageable) {
		return this.usuarioRepository.filtrar(filtro, pageable);
	}

	@Transactional(readOnly = true)
	public Optional<Usuario> findOne(Long codigoUsuario) {
		return this.usuarioRepository.findById(codigoUsuario);
	}
}
