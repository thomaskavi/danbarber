package com.thomaskavi.danbarber.dtos;

import com.thomaskavi.danbarber.enums.Modulo;
import jakarta.validation.constraints.NotNull;

public record AlterarModuloRequestDTO(
        @NotNull Modulo modulo,
        @NotNull Boolean ativar
) {}