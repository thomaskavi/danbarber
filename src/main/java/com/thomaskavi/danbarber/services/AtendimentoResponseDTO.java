package com.thomaskavi.danbarber.services;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.thomaskavi.danbarber.entities.FormaPagamento;


public record AtendimentoResponseDTO(
        Long id,
        String barbeiroNome,
        String nomeCliente,
        LocalDateTime dataHora,
        FormaPagamento formaPagamento,
        List<String> nomesServicos,
        BigDecimal valorTotal,
        BigDecimal valorComissao,
        String observacao
) {
}
