package com.thomaskavi.danbarber.dtos;

import java.math.BigDecimal;

public record ServicoResponseDTO(
        Long id,
        String nome,
        BigDecimal preco,
        boolean ativo
) {
}
