package com.thomaskavi.danbarber.services;

import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.thomaskavi.danbarber.dtos.UsuarioResponseDTO;
import com.thomaskavi.danbarber.enums.Role;
import com.thomaskavi.danbarber.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
public List<UsuarioResponseDTO> listarFuncionariosAtivos() {
        Long empresaId = empresaIdDoUsuarioLogado();
        return usuarioRepository.findByRoleAndAtivoTrueAndEmpresaId(Role.FUNCIONARIO, empresaId).stream()
                .map(u -> new UsuarioResponseDTO(u.getId(), u.getNome(), u.getPercentualComissao()))
                .toList();
    }

    private Long empresaIdDoUsuarioLogado() {
        String login = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalStateException("Usuário autenticado não encontrado"))
                .getEmpresa()
                .getId();
    }
}

