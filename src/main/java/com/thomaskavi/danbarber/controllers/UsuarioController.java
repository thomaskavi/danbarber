package com.thomaskavi.danbarber.controllers;

import com.thomaskavi.danbarber.dtos.UsuarioResponseDTO;
import com.thomaskavi.danbarber.services.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    // Só o DONO precisa dessa lista (é ele quem escolhe em nome de qual funcionario lançar)
    @PreAuthorize("hasRole('EMPREGADOR')")
    @GetMapping("/funcionarios")
    public ResponseEntity<List<UsuarioResponseDTO>> listarFuncionarios() {
        return ResponseEntity.ok(usuarioService.listarFuncionariosAtivos());
    }
}
