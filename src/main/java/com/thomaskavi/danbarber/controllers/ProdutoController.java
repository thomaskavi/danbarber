package com.thomaskavi.danbarber.controllers;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.thomaskavi.danbarber.dtos.AjusteEstoqueRequestDTO;
import com.thomaskavi.danbarber.dtos.AtualizarProdutoRequestDTO;
import com.thomaskavi.danbarber.dtos.ProdutoRequestDTO;
import com.thomaskavi.danbarber.dtos.ProdutoResponseDTO;
import com.thomaskavi.danbarber.services.ProdutoService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> registrar(@Valid @RequestBody ProdutoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(produtoService.registrar(dto));
    }

    @GetMapping
    public ResponseEntity<Page<ProdutoResponseDTO>> findAll(
            @RequestParam(name = "name", required = false) String name,
            @PageableDefault(size = 20, sort = "nome") Pageable pageable) {
        return ResponseEntity.ok(produtoService.findAll(name, pageable));
    }

    @GetMapping("/{id}")
    public ProdutoResponseDTO buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id);
    }

    @GetMapping("/inativos")
    public ResponseEntity<List<ProdutoResponseDTO>> listarInativos() {
        return ResponseEntity.ok(produtoService.listarInativos());
    }

    @PutMapping("/{id}")
    public ProdutoResponseDTO atualizar(@PathVariable Long id, @Valid @RequestBody AtualizarProdutoRequestDTO dto) {
        return produtoService.atualizar(id, dto);
    }

    @PatchMapping("/{id}/estoque")
    public ProdutoResponseDTO ajustarEstoque(@PathVariable Long id, @Valid @RequestBody AjusteEstoqueRequestDTO dto) {
        return produtoService.ajustarEstoque(id, dto);
    }

    @PatchMapping("/{id}/desativar")
    public ResponseEntity<Void> desativar(@PathVariable Long id) {
        produtoService.desativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    public ProdutoResponseDTO reativar(@PathVariable Long id) {
        return produtoService.reativar(id);
    }
}