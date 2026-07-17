package com.thomaskavi.danbarber.dtos;

import java.math.BigDecimal;

public record ProdutoResponseDTO(
        Long id,
        String nome,
        BigDecimal preco,
        Integer quantidadeEstoque,
        boolean ativo
) {}