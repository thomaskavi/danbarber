package com.thomaskavi.danbarber.dtos;

import java.util.List;

import com.thomaskavi.danbarber.enums.FormaPagamento;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record VendaRequestDTO(
        @Size(max = 75, message = "Limite máximo de  caracteres atingido!") String nomeCliente,
        @NotNull FormaPagamento formaPagamento,
        @NotEmpty @Valid List<ItemVendaRequestDTO> itens,
        @Size(max = 150, message = "Limite máximo de 150 caracteres atingido!") String observacao
) {}