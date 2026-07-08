package com.thomaskavi.danbarber.security;

import com.thomaskavi.danbarber.entities.Usuario;
import com.thomaskavi.danbarber.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + login));

        return org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getLogin())
                .password(usuario.getSenhaHash())
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRole().name())))
                .disabled(!usuario.isAtivo())
                .build();
    }
}
