package com.thomaskavi.danbarber.dtos;

import com.thomaskavi.danbarber.enums.FormaPagamento;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record VendaRequestDTO(
        String nomeCliente,
        @NotNull FormaPagamento formaPagamento,
        @NotEmpty @Valid List<ItemVendaRequestDTO> itens
) {}