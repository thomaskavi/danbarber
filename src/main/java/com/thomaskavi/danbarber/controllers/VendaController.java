package com.thomaskavi.danbarber.controllers;

import com.thomaskavi.danbarber.dtos.VendaRequestDTO;
import com.thomaskavi.danbarber.dtos.VendaResponseDTO;
import com.thomaskavi.danbarber.services.AutenticacaoService;
import com.thomaskavi.danbarber.services.VendaService;
import com.thomaskavi.danbarber.entities.Usuario;
import com.thomaskavi.danbarber.enums.Role;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/vendas")
@RequiredArgsConstructor
public class VendaController {

    private final VendaService vendaService;
    private final AutenticacaoService autenticacaoService;

    @PostMapping
    public ResponseEntity<VendaResponseDTO> registrar(@Valid @RequestBody VendaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(vendaService.registrar(dto));
    }

    @GetMapping
    public List<VendaResponseDTO> listar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {

        Usuario logado = autenticacaoService.obterUsuarioLogado();
        if (logado.getRole() == Role.FUNCIONARIO) {
            return vendaService.listarMinhasVendas(inicio, fim);
        }
        return vendaService.listarPorPeriodo(inicio, fim);
    }
}