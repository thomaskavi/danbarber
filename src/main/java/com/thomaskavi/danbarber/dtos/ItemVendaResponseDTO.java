package com.thomaskavi.danbarber.dtos;

import java.math.BigDecimal;

public record ItemVendaResponseDTO(
        String nomeProduto,
        Integer quantidade,
        BigDecimal precoUnitarioCobrado,
        BigDecimal subtotal
) {}