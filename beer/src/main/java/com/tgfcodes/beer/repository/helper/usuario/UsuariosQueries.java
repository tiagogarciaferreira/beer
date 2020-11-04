package com.tgfcodes.beer.repository.helper.usuario;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.tgfcodes.beer.model.Usuario;
import com.tgfcodes.beer.repository.filter.UsuarioFilter;

public interface UsuariosQueries {

	public Optional<Usuario> findByEmailAtivo(String email);
	public List<String> permissoes(Usuario usuario);
	public Page<Usuario> filtrar(UsuarioFilter filtro, Pageable pageable);
}
