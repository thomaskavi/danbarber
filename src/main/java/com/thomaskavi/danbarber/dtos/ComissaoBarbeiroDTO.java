package com.thomaskavi.danbarber.dtos;

import java.math.BigDecimal;

public record ComissaoBarbeiroDTO(
        Long barbeiroId,
        String nomeBarbeiro,
        BigDecimal totalComissao
) {
}
