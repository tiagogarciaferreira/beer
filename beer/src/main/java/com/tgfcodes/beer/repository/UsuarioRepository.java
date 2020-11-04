package com.tgfcodes.beer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.tgfcodes.beer.model.Usuario;
import com.tgfcodes.beer.repository.helper.usuario.UsuariosQueries;


@Repository
@Transactional
public interface UsuarioRepository extends JpaRepository<Usuario, Long>, UsuariosQueries {

	Optional<Usuario> findByEmailIgnoreCase(String email);
	public List<Usuario> findByCodigoIn(Long[] codigos);

}
