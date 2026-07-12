package com.thomaskavi.danbarber.controllers;

import com.thomaskavi.danbarber.dtos.CriarBarbeiroRequestDTO;
import com.thomaskavi.danbarber.dtos.CriarUsuarioRequestDTO;
import com.thomaskavi.danbarber.dtos.LoginRequestDTO;
import com.thomaskavi.danbarber.dtos.LoginResponseDTO;
import com.thomaskavi.danbarber.services.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

      // NOVO MÉTODO: Aberto ao público para registrar novos usuários/donos
    @PostMapping("/register")
    public ResponseEntity<Void> registrarUsuario(@Valid @RequestBody CriarUsuarioRequestDTO dto) {
        authService.registrarUsuario(dto); // Você precisará criar este método no AuthService
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    
    // Só o DONO logado pode cadastrar novos barbeiros
    @PostMapping("/barbeiros")
    @PreAuthorize("hasRole('DONO')")
    public ResponseEntity<Void> registrarBarbeiro(@Valid @RequestBody CriarBarbeiroRequestDTO dto) {
        authService.registrarBarbeiro(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}
