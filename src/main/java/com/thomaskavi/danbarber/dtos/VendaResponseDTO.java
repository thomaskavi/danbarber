package com.thomaskavi.danbarber.dtos;

import com.thomaskavi.danbarber.enums.FormaPagamento;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record VendaResponseDTO(
        Long id,
        String nomeVendedor,
        String nomeCliente,
        LocalDateTime dataHora,
        FormaPagamento formaPagamento,
        List<ItemVendaResponseDTO> itens,
        BigDecimal valorTotal,
        BigDecimal valorComissao
) {}
