package com.thomaskavi.danbarber.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thomaskavi.danbarber.dtos.DespesaRequestDTO;
import com.thomaskavi.danbarber.dtos.DespesaResponseDTO;
import com.thomaskavi.danbarber.services.DespesaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/despesas")
@RequiredArgsConstructor
public class DespesaController {

    private final DespesaService despesaService;

    @PostMapping
    public ResponseEntity<DespesaResponseDTO> registrar(@Valid @RequestBody DespesaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(despesaService.registrar(dto));
    }

    // Ex: GET /api/despesas?inicio=2026-07-01&fim=2026-07-31
    @GetMapping
    public ResponseEntity<List<DespesaResponseDTO>> listarPorPeriodo(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fim) {
        return ResponseEntity.ok(despesaService.listarPorPeriodo(inicio, fim));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DespesaResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody DespesaRequestDTO dto) {

        return ResponseEntity.ok(despesaService.atualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        despesaService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}
