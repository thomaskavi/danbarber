package com.thomaskavi.danbarber.services;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.thomaskavi.danbarber.entities.Usuario;
import com.thomaskavi.danbarber.repositories.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;

    // O "username" salvo no contexto de segurança é o login (veja UsuarioDetailsServiceImpl)
    public Usuario obterUsuarioLogado() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new EntityNotFoundException("Usuário autenticado não encontrado: " + login));
    }
}
