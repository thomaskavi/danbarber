package com.thomaskavi.danbarber.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record CriarBarbeiroRequestDTO(

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O login é obrigatório")
        String login,

        @NotBlank(message = "A senha é obrigatória")
        String senha,

        @NotNull(message = "O percentual de comissão é obrigatório")
        @Positive(message = "O percentual de comissão deve ser maior que zero")
        BigDecimal percentualComissao
) {
}
