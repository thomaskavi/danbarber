package com.thomaskavi.danbarber.controllers;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thomaskavi.danbarber.dtos.AtendimentoRequestDTO;
import com.thomaskavi.danbarber.dtos.AtendimentoResponseDTO;
import com.thomaskavi.danbarber.services.AtendimentoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/atendimentos")
@RequiredArgsConstructor
public class AtendimentoController {

    private final AtendimentoService atendimentoService;

    @PostMapping
    public ResponseEntity<AtendimentoResponseDTO> registrar(@Valid @RequestBody AtendimentoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(atendimentoService.registrar(dto));
    }

    // Ex: GET /api/atendimentos?inicio=2026-07-01T00:00:00&fim=2026-07-31T23:59:59
     // Só o DONO vê o consolidado de todos os barbeiros
    @PreAuthorize("hasRole('DONO')")
    @GetMapping
    public ResponseEntity<List<AtendimentoResponseDTO>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(atendimentoService.listarPorPeriodo(inicio, fim));
    }

    // Só o DONO consulta atendimentos de um barbeiro específico por ID
    @PreAuthorize("hasRole('DONO')")
    @GetMapping("/barbeiro/{barbeiroId}")
    public ResponseEntity<List<AtendimentoResponseDTO>> listarPorBarbeiroEPeriodo(
            @PathVariable Long barbeiroId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(
                atendimentoService.listarPorBarbeiroEPeriodo(barbeiroId, inicio, fim));
    }

    // Qualquer barbeiro logado vê os PRÓPRIOS atendimentos, sem precisar saber o próprio ID
    @GetMapping("/meus")
    public ResponseEntity<List<AtendimentoResponseDTO>> listarMeusAtendimentos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return ResponseEntity.ok(atendimentoService.listarMeusAtendimentos(inicio, fim));
    }
}
