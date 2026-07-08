package com.thomaskavi.danbarber.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public record FechamentoMensalDTO(
        LocalDate inicio,
        LocalDate fim,
        BigDecimal faturamentoTotal,
        Map<String, BigDecimal> faturamentoPorFormaPagamento,
        List<ComissaoBarbeiroDTO> comissoesPorBarbeiro,
        BigDecimal totalComissoes,
        BigDecimal totalDespesas,
        BigDecimal saldoLiquido // faturamento - comissões - despesas
) {
}
