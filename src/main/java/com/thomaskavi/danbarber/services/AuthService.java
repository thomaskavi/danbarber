package com.thomaskavi.danbarber.services;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.thomaskavi.danbarber.dtos.CriarBarbeiroRequestDTO;
import com.thomaskavi.danbarber.dtos.LoginRequestDTO;
import com.thomaskavi.danbarber.dtos.LoginResponseDTO;
import com.thomaskavi.danbarber.entities.Usuario;
import com.thomaskavi.danbarber.enums.Role;
import com.thomaskavi.danbarber.repositories.UsuarioRepository;
import com.thomaskavi.danbarber.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        // Lança exceção automaticamente se login/senha estiverem errados
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.login(), dto.senha())
        );

        Usuario usuario = usuarioRepository.findByLogin(dto.login())
                .orElseThrow(() -> new IllegalStateException("Usuário não encontrado após autenticação"));

        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(usuario.getLogin())
                .password(usuario.getSenhaHash())
                .authorities("ROLE_" + usuario.getRole().name())
                .build();

        String token = jwtService.generateToken(
                userDetails,
                Map.of("nome", usuario.getNome(), "role", usuario.getRole().name())
        );

        return new LoginResponseDTO(token, usuario.getNome(), usuario.getRole().name());
    }

    public void registrarBarbeiro(CriarBarbeiroRequestDTO dto) {
        if (usuarioRepository.existsByLogin(dto.login())) {
            throw new IllegalArgumentException("Já existe um usuário com esse login");
        }

        Usuario barbeiro = Usuario.builder()
                .nome(dto.nome())
                .login(dto.login())
                .senhaHash(passwordEncoder.encode(dto.senha()))
                .role(Role.BARBEIRO)
                .percentualComissao(dto.percentualComissao())
                .ativo(true)
                .build();

        usuarioRepository.save(barbeiro);
    }
}
