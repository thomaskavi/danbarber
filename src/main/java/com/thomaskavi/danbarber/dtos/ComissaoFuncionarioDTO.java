package com.thomaskavi.danbarber.dtos;

import java.math.BigDecimal;

public record ComissaoFuncionarioDTO(
        Long funcionarioId,
        String nomeFuncionario,
        BigDecimal totalComissao
) {
}
