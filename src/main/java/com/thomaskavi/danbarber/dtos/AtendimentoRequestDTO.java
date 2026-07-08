package com.thomaskavi.danbarber.dtos;

import java.util.List;

import com.thomaskavi.danbarber.enums.FormaPagamento;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record AtendimentoRequestDTO(

        // Opcional: só é usado quando o DONO está lançando em nome de um barbeiro.
        // Quando um BARBEIRO faz a requisição, esse campo é ignorado —
        // o barbeiro é sempre identificado pelo token de autenticação.
        Long barbeiroId,

        String nomeCliente,

        @NotNull(message = "A forma de pagamento é obrigatória")
        FormaPagamento formaPagamento,

        @NotEmpty(message = "Selecione ao menos um serviço")
        List<Long> servicoIds,

        String observacao
) {
}
