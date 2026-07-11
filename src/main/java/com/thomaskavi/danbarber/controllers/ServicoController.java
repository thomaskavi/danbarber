package com.thomaskavi.danbarber.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thomaskavi.danbarber.dtos.ServicoRequestDTO;
import com.thomaskavi.danbarber.dtos.ServicoResponseDTO;
import com.thomaskavi.danbarber.services.ServicoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/servicos")
@RequiredArgsConstructor
public class ServicoController {

    private final ServicoService servicoService;

    // Só o DONO decide preço e catálogo de serviços
    @PreAuthorize("hasRole('DONO')")
    @PostMapping
    public ResponseEntity<ServicoResponseDTO> criar(@Valid @RequestBody ServicoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(servicoService.registrar(dto));
    }

    // Aberto para DONO e BARBEIRO: o barbeiro precisa ver os serviços pra montar um atendimento
    @GetMapping
    public ResponseEntity<List<ServicoResponseDTO>> listarAtivos() {
        return ResponseEntity.ok(servicoService.listarAtivos());
    }

    // Só o DONO precisa ver o que está desativado, pra decidir se reativa
    @PreAuthorize("hasRole('DONO')")
    @GetMapping("/inativos")
    public ResponseEntity<List<ServicoResponseDTO>> listarInativos() {
        return ResponseEntity.ok(servicoService.listarInativos());
    }

    @PreAuthorize("hasRole('DONO')")
    @PutMapping("/{id}")
    public ResponseEntity<ServicoResponseDTO> atualizar(
            @PathVariable Long id, @Valid @RequestBody ServicoRequestDTO dto) {
        return ResponseEntity.ok(servicoService.atualizar(id, dto));
    }

    @PreAuthorize("hasRole('DONO')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        servicoService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('DONO')")
    @PatchMapping("/{id}/reativar")
    public ResponseEntity<ServicoResponseDTO> reativar(@PathVariable Long id) {
        return ResponseEntity.ok(servicoService.reativar(id));
    }
}
