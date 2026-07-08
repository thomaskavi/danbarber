package com.thomaskavi.danbarber.controllers;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thomaskavi.danbarber.dtos.FechamentoMensalDTO;
import com.thomaskavi.danbarber.services.FechamentoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/fechamento")
@RequiredArgsConstructor
public class FechamentoController {

    private final FechamentoService fechamentoService;

    // Ex: GET /api/fechamento?inicio=2026-07-01&fim=2026-07-31
    @PreAuthorize("hasRole('DONO')")
    @GetMapping
    public ResponseEntity<FechamentoMensalDTO> gerarFechamento(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(fechamentoService.gerarFechamento(inicio, fim));
    }
}
