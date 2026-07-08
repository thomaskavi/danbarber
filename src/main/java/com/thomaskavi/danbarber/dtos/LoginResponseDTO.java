package com.thomaskavi.danbarber.dtos;

public record LoginResponseDTO(
        String token,
        String nome,
        String role
) {
}
