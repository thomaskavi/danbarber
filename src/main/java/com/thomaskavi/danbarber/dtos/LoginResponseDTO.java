package com.thomaskavi.danbarber.dtos;

import java.util.Set;

import com.thomaskavi.danbarber.enums.Modulo;

public record LoginResponseDTO(
        String token,
        String nome,
        String role,
        Set<Modulo> modulosAtivos
) {
}
