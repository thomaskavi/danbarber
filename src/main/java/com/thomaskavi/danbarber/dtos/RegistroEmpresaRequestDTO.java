package com.thomaskavi.danbarber.dtos;

import com.thomaskavi.danbarber.enums.Ramo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RegistroEmpresaRequestDTO(
        @NotBlank String nomeEmpresa,
        @NotNull Ramo ramo,
        @NotBlank String nomeDono,
        @NotBlank String login,
        @NotBlank String senha
) {}