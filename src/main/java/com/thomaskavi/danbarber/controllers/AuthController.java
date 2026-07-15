package com.thomaskavi.danbarber.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thomaskavi.danbarber.dtos.CriarFuncionarioRequestDTO;
import com.thomaskavi.danbarber.dtos.LoginRequestDTO;
import com.thomaskavi.danbarber.dtos.LoginResponseDTO;
import com.thomaskavi.danbarber.dtos.RegistroEmpresaRequestDTO;
import com.thomaskavi.danbarber.services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://danbarber-app.netlify.app")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

  @PostMapping("/register")
    public ResponseEntity<Void> registrarEmpresa(@Valid @RequestBody RegistroEmpresaRequestDTO dto) {
        authService.registrarEmpresa(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/funcionarios/register")
    @PreAuthorize("hasRole('EMPREGADOR')")
    public ResponseEntity<Void> registrarFuncionario(@Valid @RequestBody CriarFuncionarioRequestDTO dto) {
        authService.registrarFuncionario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
