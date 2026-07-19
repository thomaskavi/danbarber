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
import com.thomaskavi.danbarber.entities.Empresa;
import com.thomaskavi.danbarber.entities.Usuario;
import com.thomaskavi.danbarber.enums.Modulo;
import com.thomaskavi.danbarber.enums.Role;
import com.thomaskavi.danbarber.repositories.AtendimentoRepository;
import com.thomaskavi.danbarber.repositories.DespesaRepository;
import com.thomaskavi.danbarber.repositories.UsuarioRepository;
import com.thomaskavi.danbarber.repositories.VendaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FechamentoService {

    private final AtendimentoRepository atendimentoRepository;
    private final VendaRepository vendaRepository;
    private final DespesaRepository despesaRepository;
    private final UsuarioRepository usuarioRepository;
    private final AutenticacaoService authService;

    public FechamentoMensalDTO gerarFechamento(LocalDate inicio, LocalDate fim) {

        Usuario usuario = authService.obterUsuarioLogado();
        Empresa empresa = usuario.getEmpresa();

        Long empresaId = empresa.getId();

        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime fimDateTime = LocalDateTime.of(fim, LocalTime.MAX);

        boolean temAtendimentos = empresa.getModulosAtivos().contains(Modulo.ATENDIMENTOS);
        boolean temVendas = empresa.getModulosAtivos().contains(Modulo.ESTOQUE_VENDAS);

        BigDecimal faturamentoAtendimentos = BigDecimal.ZERO;
        BigDecimal faturamentoVendas = BigDecimal.ZERO;

        Map<String, BigDecimal> faturamentoPorForma = Map.of();

        if (temAtendimentos) {
            faturamentoPorForma = atendimentoRepository
                    .somarTotalPorFormaPagamento(empresaId, inicioDateTime, fimDateTime)
                    .stream()
                    .collect(Collectors.toMap(
                            item -> item.getFormaPagamento().name(),
                            item -> item.getTotal()));

            faturamentoAtendimentos = faturamentoPorForma
                    .values()
                    .stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        if (temVendas) {
            faturamentoVendas = vendaRepository.somarValorTotalPeriodo(empresaId, inicioDateTime, fimDateTime);
        }

        BigDecimal faturamentoTotal = faturamentoAtendimentos.add(faturamentoVendas);

        // Considera qualquer funcionário que teve atendimento OU venda no período —
        // antes, quem só vendia produto (sem nenhum atendimento) ficava de fora da lista inteira
        List<Usuario> todosFuncionarios = usuarioRepository
                .findByRoleAndAtivoTrueAndEmpresaId(Role.FUNCIONARIO, empresaId);

        List<Usuario> funcionariosComMovimento = todosFuncionarios.stream()
                .filter(funcionario -> {
                    boolean teveAtendimento = temAtendimentos && !atendimentoRepository
                            .findByFuncionarioIdAndFuncionario_Empresa_IdAndDataHoraBetween(
                                    funcionario.getId(), empresaId, inicioDateTime, fimDateTime)
                            .isEmpty();

                    boolean teveVenda = temVendas && !vendaRepository
                            .findByVendedorIdAndVendedor_Empresa_IdAndDataHoraBetween(
                                    funcionario.getId(), empresaId, inicioDateTime, fimDateTime)
                            .isEmpty();

                    return teveAtendimento || teveVenda;
                })
                .toList();

        List<ComissaoFuncionarioDTO> comissoes = funcionariosComMovimento.stream()
                .map(funcionario -> {

                    BigDecimal comissaoAtendimentos = temAtendimentos
                            ? atendimentoRepository.somarComissaoPorFuncionarioEPeriodo(
                                    funcionario.getId(), empresaId, inicioDateTime, fimDateTime)
                            : BigDecimal.ZERO;

                    BigDecimal comissaoVendas = temVendas
                            ? vendaRepository.somarComissaoPorVendedorEPeriodo(
                                    funcionario.getId(), empresaId, inicioDateTime, fimDateTime)
                            : BigDecimal.ZERO;

                    BigDecimal totalComissao = comissaoAtendimentos.add(comissaoVendas);

                    return new ComissaoFuncionarioDTO(
                            funcionario.getId(),
                            funcionario.getNome(),
                            totalComissao);
                })
                .toList();

        BigDecimal totalComissoes = comissoes.stream()
                .map(ComissaoFuncionarioDTO::totalComissao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalDespesas = despesaRepository.somarDespesasPorPeriodo(empresaId, inicio, fim);

        BigDecimal saldoLiquido = faturamentoTotal
                .subtract(totalComissoes)
                .subtract(totalDespesas);

        return new FechamentoMensalDTO(
                inicio,
                fim,
                faturamentoAtendimentos,
                faturamentoVendas,
                faturamentoTotal,
                faturamentoPorForma,
                comissoes,
                totalComissoes,
                totalDespesas,
                saldoLiquido
        );
    }

}
