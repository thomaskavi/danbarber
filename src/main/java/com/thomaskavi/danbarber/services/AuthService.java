package com.thomaskavi.danbarber.services;

import java.util.Map;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.thomaskavi.danbarber.dtos.CriarFuncionarioRequestDTO;
import com.thomaskavi.danbarber.dtos.LoginRequestDTO;
import com.thomaskavi.danbarber.dtos.LoginResponseDTO;
import com.thomaskavi.danbarber.dtos.RegistroEmpresaRequestDTO;
import com.thomaskavi.danbarber.entities.Empresa;
import com.thomaskavi.danbarber.entities.Usuario;
import com.thomaskavi.danbarber.enums.Role;
import com.thomaskavi.danbarber.repositories.EmpresaRepository;
import com.thomaskavi.danbarber.repositories.UsuarioRepository;
import com.thomaskavi.danbarber.security.JwtService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AutenticacaoService autenticacaoService;

    public LoginResponseDTO login(LoginRequestDTO dto) {
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

        Long empresaId = (usuario.getEmpresa() != null) ? usuario.getEmpresa().getId() : null;
        
        String token = jwtService.generateToken(
                userDetails,
                Map.of(
                "nome", usuario.getNome(),
                "role", usuario.getRole().name(),
                "empresaId", (empresaId != null) ? empresaId : 0L
            )
        );

        return new LoginResponseDTO(token, usuario.getNome(), usuario.getRole().name());
    }

    // Cadastro público: cria a empresa E o dono juntos, numa transação só
    @org.springframework.transaction.annotation.Transactional
    public void registrarEmpresa(RegistroEmpresaRequestDTO dto) {
        if (usuarioRepository.existsByLogin(dto.login())) {
            throw new IllegalArgumentException("Já existe um usuário com esse login");
        }

        Empresa empresa = Empresa.builder()
                .nome(dto.nomeEmpresa())
                .ramo(dto.ramo())
                .ativa(true)
                .build();
        empresa = empresaRepository.save(empresa);

        Usuario dono = Usuario.builder()
                .nome(dto.nomeDono())
                .login(dto.login())
                .senhaHash(passwordEncoder.encode(dto.senha()))
                .role(Role.EMPREGADOR) // sempre EMPREGADOR, nunca lido do body
                .empresa(empresa)
                .percentualComissao(null)
                .ativo(true)
                .build();

        usuarioRepository.save(dono);
    }

    // Só DONO autenticado chama isso — funcionario herda a empresa de quem tá logado
    public void registrarFuncionario(CriarFuncionarioRequestDTO dto) {
        Usuario donoLogado = autenticacaoService.obterUsuarioLogado();

        if (usuarioRepository.existsByLogin(dto.login())) {
            throw new IllegalArgumentException("Já existe um usuário com esse login");
        }

        Usuario funcionario = Usuario.builder()
                .nome(dto.nome())
                .login(dto.login())
                .senhaHash(passwordEncoder.encode(dto.senha()))
                .role(Role.FUNCIONARIO)
                .empresa(donoLogado.getEmpresa()) // herdado, nunca vindo do request
                .percentualComissao(dto.percentualComissao())
                .ativo(true)
                .build();

        usuarioRepository.save(funcionario);
    }
}