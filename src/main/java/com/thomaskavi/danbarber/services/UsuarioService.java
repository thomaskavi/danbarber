package com.thomaskavi.danbarber.services;

import com.thomaskavi.danbarber.dtos.UsuarioResponseDTO;
import com.thomaskavi.danbarber.enums.Role;
import com.thomaskavi.danbarber.repositories.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public List<UsuarioResponseDTO> listarBarbeirosAtivos() {
        return usuarioRepository.findByRoleAndAtivoTrue(Role.BARBEIRO).stream()
                .map(u -> new UsuarioResponseDTO(u.getId(), u.getNome(), u.getPercentualComissao()))
                .toList();
    }
}
