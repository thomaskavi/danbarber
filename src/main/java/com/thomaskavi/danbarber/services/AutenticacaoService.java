package com.thomaskavi.danbarber.services;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.thomaskavi.danbarber.entities.Empresa;
import com.thomaskavi.danbarber.entities.Usuario;
import com.thomaskavi.danbarber.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AutenticacaoService {

    private final UsuarioRepository usuarioRepository;

    public Usuario obterUsuarioLogado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        return usuarioRepository.findByLogin(auth.getName())
                .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado"));
    }
    
    public Long obterEmpresaIdLogado() {
    return obterUsuarioLogado().getEmpresa().getId();
    }

    public Empresa obterEmpresaLogada() {
        return obterUsuarioLogado().getEmpresa();
    }
}
