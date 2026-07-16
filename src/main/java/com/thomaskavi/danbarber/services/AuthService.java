package com.thomaskavi.danbarber.services;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.thomaskavi.danbarber.dtos.CriarFuncionarioRequestDTO;
import com.thomaskavi.danbarber.dtos.LoginRequestDTO;
import com.thomaskavi.danbarber.dtos.LoginResponseDTO;
import com.thomaskavi.danbarber.dtos.RegistroEmpresaRequestDTO;
import com.thomaskavi.danbarber.entities.Empresa;
import com.thomaskavi.danbarber.entities.Usuario;
import com.thomaskavi.danbarber.enums.Modulo;
import com.thomaskavi.danbarber.enums.Ramo;
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
        Set<Modulo> modulos = usuario.getEmpresa() != null ? usuario.getEmpresa().getModulosAtivos() : Set.of();

        String token = jwtService.generateToken(
                userDetails,
                Map.of(
                "nome", usuario.getNome(),
                "role", usuario.getRole().name(),
                "empresaId", (empresaId != null) ? empresaId : 0L
            )
        );

        return new LoginResponseDTO(token, usuario.getNome(), usuario.getRole().name(), modulos);
    }

    @org.springframework.transaction.annotation.Transactional
public void registrarEmpresa(RegistroEmpresaRequestDTO dto) {
    if (usuarioRepository.existsByLogin(dto.login())) {
        throw new IllegalArgumentException("Já existe um usuário com esse login");
    }

    Empresa empresa = Empresa.builder()
            .nome(dto.nomeEmpresa())
            .ramo(dto.ramo())
            .modulosAtivos(modulosPadraoPorRamo(dto.ramo()))
            .ativa(true)
            .build();
    empresa = empresaRepository.save(empresa);

    Usuario empregador = Usuario.builder()
            .nome(dto.nomeDono())
            .login(dto.login())
            .senhaHash(passwordEncoder.encode(dto.senha()))
            .role(Role.EMPREGADOR)
            .empresa(empresa)
            .percentualComissao(null)
            .ativo(true)
            .build();

    usuarioRepository.save(empregador);
}

private Set<Modulo> modulosPadraoPorRamo(Ramo ramo) {
    return switch (ramo) {
        case ATENDIMENTO -> Set.of(Modulo.ATENDIMENTOS);
        case VENDAS -> Set.of(Modulo.ESTOQUE_VENDAS);
        case OUTRO -> Set.of();
    };
}

    public void registrarFuncionario(CriarFuncionarioRequestDTO dto) {
    Usuario donoLogado = autenticacaoService.obterUsuarioLogado();

    if (usuarioRepository.existsByLogin(dto.login())) {
        throw new IllegalArgumentException("Já existe um usuário com esse login");
    }

    if (Boolean.TRUE.equals(dto.temComissao())
            && (dto.percentualComissao() == null || dto.percentualComissao().compareTo(BigDecimal.ZERO) <= 0)) {
        throw new IllegalArgumentException("Informe um percentual de comissão válido");
    }

    BigDecimal comissaoFinal = Boolean.TRUE.equals(dto.temComissao()) ? dto.percentualComissao() : null;

    Usuario funcionario = Usuario.builder()
            .nome(dto.nome())
            .login(dto.login())
            .senhaHash(passwordEncoder.encode(dto.senha()))
            .role(Role.FUNCIONARIO)
            .empresa(donoLogado.getEmpresa())
            .percentualComissao(comissaoFinal) // null se temComissao = false, garantindo consistência
            .ativo(true)
            .build();

    usuarioRepository.save(funcionario);
}
}