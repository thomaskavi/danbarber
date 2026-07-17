package com.thomaskavi.danbarber.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.thomaskavi.danbarber.enums.FormaPagamento;


public record AtendimentoResponseDTO(
        Long id,
        String funcionarioNome,
        String nomeCliente,
        LocalDateTime dataHora,
        FormaPagamento formaPagamento,
        List<String> nomesServicos,
        BigDecimal valorTotal,
        BigDecimal valorComissao,
        String observacao
) {
}
