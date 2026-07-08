package com.thomaskavi.danbarber.dtos;

import java.util.List;

import com.thomaskavi.danbarber.enums.FormaPagamento;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AtendimentoRequestDTO(

        @NotNull(message = "O barbeiro é obrigatório")
        Long barbeiroId,

        String nomeCliente,

        @NotNull(message = "A forma de pagamento é obrigatória")
        FormaPagamento formaPagamento,

        @NotEmpty(message = "Selecione ao menos um serviço")
        List<Long> servicoIds,

        String observacao
) {
}
