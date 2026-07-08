package com.thomaskavi.danbarber.services;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public record DespesaRequestDTO(

        @NotNull(message = "A descrição é obrigatória")
        String descricao,

        @NotNull(message = "O valor é obrigatório")
        BigDecimal valor,

        @NotNull(message = "A data é obrigatória")
        LocalDate data,

        String categoria
) {
}
