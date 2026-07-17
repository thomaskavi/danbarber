package com.thomaskavi.danbarber.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record ProdutoRequestDTO(
        @NotBlank String nome,
        @NotNull @Positive BigDecimal preco,
        @NotNull @PositiveOrZero Integer quantidadeEstoque
) {}