package com.thomaskavi.danbarber.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.thomaskavi.danbarber.dtos.ComissaoFuncionarioDTO;
import com.thomaskavi.danbarber.dtos.FechamentoMensalDTO;
import com.thomaskavi.danbarber.entities.Usuario;
import com.thomaskavi.danbarber.enums.Role;
import com.thomaskavi.danbarber.repositories.AtendimentoRepository;
import com.thomaskavi.danbarber.repositories.DespesaRepository;
import com.thomaskavi.danbarber.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FechamentoService {

    private final AtendimentoRepository atendimentoRepository;
    private final DespesaRepository despesaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AutenticacaoService authService;

    public FechamentoMensalDTO gerarFechamento(LocalDate inicio, LocalDate fim) {

            Long empresaId = authService.obterEmpresaIdLogado();

            LocalDateTime inicioDateTime = inicio.atStartOfDay();
            LocalDateTime fimDateTime = LocalDateTime.of(fim, LocalTime.MAX);

            // 1. Faturamento
            Map<String, BigDecimal> faturamentoPorForma = atendimentoRepository
                            .somarTotalPorFormaPagamento(
                                            empresaId,
                                            inicioDateTime,
                                            fimDateTime)
                            .stream()
                            .collect(Collectors.toMap(
                                            item -> item.getFormaPagamento().name(),
                                            item -> item.getTotal()));

            BigDecimal faturamentoTotal = faturamentoPorForma.values()
                            .stream()
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 2. Funcionarios da empresa
            List<Usuario> funcionarios = usuarioRepository
                            .findByRoleAndAtivoTrueAndEmpresaId(Role.FUNCIONARIO, empresaId)
                            .stream()
                            .filter(funcionario -> !atendimentoRepository
                                            .findByFuncionarioIdAndFuncionario_Empresa_IdAndDataHoraBetween(
                                                            funcionario.getId(),
                                                            empresaId,
                                                            inicioDateTime,
                                                            fimDateTime)
                                            .isEmpty())
                            .toList();

            List<ComissaoFuncionarioDTO> comissoes = funcionarios.stream()
                            .map(funcionario -> {

                                    BigDecimal totalComissao = atendimentoRepository.somarComissaoPorFuncionarioEPeriodo(
                                                    funcionario.getId(),
                                                    empresaId,
                                                    inicioDateTime,
                                                    fimDateTime);

                                    return new ComissaoFuncionarioDTO(
                                                    funcionario.getId(),
                                                    funcionario.getNome(),
                                                    totalComissao);
                            })
                            .toList();

            BigDecimal totalComissoes = comissoes.stream()
                            .map(ComissaoFuncionarioDTO::totalComissao)
                            .reduce(BigDecimal.ZERO, BigDecimal::add);

            // 3. Despesas
            BigDecimal totalDespesas = despesaRepository.somarDespesasPorPeriodo(
                            empresaId,
                            inicio,
                            fim);

            // 4. Lucro
            BigDecimal saldoLiquido = faturamentoTotal
                            .subtract(totalComissoes)
                            .subtract(totalDespesas);

            return new FechamentoMensalDTO(
                            inicio,
                            fim,
                            faturamentoTotal,
                            faturamentoPorForma,
                            comissoes,
                            totalComissoes,
                            totalDespesas,
                            saldoLiquido);
    }
}
