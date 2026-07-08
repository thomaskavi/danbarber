package com.thomaskavi.danbarber.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomaskavi.danbarber.entities.Usuario;
import com.thomaskavi.danbarber.enums.Role;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByLogin(String login);

    boolean existsByLogin(String login);

    List<Usuario> findByRoleAndAtivoTrue(Role role);
}
