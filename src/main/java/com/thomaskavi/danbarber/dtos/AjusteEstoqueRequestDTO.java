package com.thomaskavi.danbarber.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AjusteEstoqueRequestDTO(
        @NotNull @Positive Integer quantidade,
        @NotNull TipoAjusteEstoque tipo // ENTRADA ou SAIDA
) {}