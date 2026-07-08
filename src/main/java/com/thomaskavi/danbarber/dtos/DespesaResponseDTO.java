package com.thomaskavi.danbarber.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DespesaResponseDTO(
        Long id,
        String descricao,
        BigDecimal valor,
        LocalDate data,
        String categoria
) {
}
