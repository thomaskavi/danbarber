package com.thomaskavi.danbarber.services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.thomaskavi.danbarber.dtos.ComissaoBarbeiroDTO;
import com.thomaskavi.danbarber.dtos.FechamentoMensalDTO;
import com.thomaskavi.danbarber.entities.Role;
import com.thomaskavi.danbarber.entities.Usuario;
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

    @SuppressWarnings("null")
public FechamentoMensalDTO gerarFechamento(LocalDate inicio, LocalDate fim) {

        LocalDateTime inicioDateTime = inicio.atStartOfDay();
        LocalDateTime fimDateTime = LocalDateTime.of(fim, LocalTime.MAX);

        // 1. Faturamento total por forma de pagamento (Pix, cartão, dinheiro)
        Map<String, BigDecimal> faturamentoPorForma =
                atendimentoRepository.somarTotalPorFormaPagamento(inicioDateTime, fimDateTime).stream()
                        .collect(Collectors.toMap(
                                item -> item.getFormaPagamento().name(),
                                item -> item.getTotal()
                        ));

        BigDecimal faturamentoTotal = faturamentoPorForma.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 2. Comissão de cada barbeiro ativo no período
        List<Usuario> barbeiros = usuarioRepository.findByRoleAndAtivoTrue(Role.BARBEIRO);

        List<ComissaoBarbeiroDTO> comissoes = barbeiros.stream()
                .map(barbeiro -> {
                    BigDecimal totalComissao = atendimentoRepository.somarComissaoPorBarbeiroEPeriodo(
                            barbeiro.getId(), inicioDateTime, fimDateTime);
                    return new ComissaoBarbeiroDTO(barbeiro.getId(), barbeiro.getNome(), totalComissao);
                })
                .toList();

        BigDecimal totalComissoes = comissoes.stream()
                .map(ComissaoBarbeiroDTO::totalComissao)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 3. Despesas do período
        BigDecimal totalDespesas = despesaRepository.somarDespesasPorPeriodo(inicio, fim);

        // 4. Saldo líquido que sobra para o dono
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
                saldoLiquido
        );
    }
}
