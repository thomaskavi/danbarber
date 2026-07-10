package com.thomaskavi.danbarber.dtos;

import java.math.BigDecimal;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        BigDecimal percentualComissao
) {
}
