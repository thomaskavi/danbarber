package com.thomaskavi.danbarber.services;

import java.math.BigDecimal;

public record ComissaoBarbeiroDTO(
        Long barbeiroId,
        String nomeBarbeiro,
        BigDecimal totalComissao
) {
}
