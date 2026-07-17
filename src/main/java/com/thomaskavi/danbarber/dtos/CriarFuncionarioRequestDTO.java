package com.thomaskavi.danbarber.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CriarFuncionarioRequestDTO(

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O login é obrigatório")
        String login,

        @NotBlank(message = "A senha é obrigatória")
        String senha,

        @NotNull(message = "Informe se o funcionário recebe comissão")
        Boolean temComissao,

        // Só validado no service, condicionalmente (obrigatório apenas se temComissao = true)
        BigDecimal percentualComissao
) {
}