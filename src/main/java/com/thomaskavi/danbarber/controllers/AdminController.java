package com.thomaskavi.danbarber.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.thomaskavi.danbarber.dtos.AlterarModuloRequestDTO;
import com.thomaskavi.danbarber.entities.Empresa;
import com.thomaskavi.danbarber.repositories.EmpresaRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final EmpresaRepository empresaRepository;

    @PreAuthorize("hasRole('ADMIN')")    
    @GetMapping("/empresas")
    public List<Empresa> listarEmpresas() {
        return empresaRepository.findAll();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/empresas/{id}/desativar")
    public ResponseEntity<Void> desativarEmpresa(@PathVariable Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));
        empresa.setAtiva(false);
        empresaRepository.save(empresa);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/empresas/{id}/ativar")
    public ResponseEntity<Void> ativarEmpresa(@PathVariable Long id) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));
        empresa.setAtiva(true);
        empresaRepository.save(empresa);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/empresas/{id}/modulos")
    public ResponseEntity<Void> alterarModulo(@PathVariable Long id, @Valid @RequestBody AlterarModuloRequestDTO dto) {
        Empresa empresa = empresaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Empresa não encontrada"));

        if (Boolean.TRUE.equals(dto.ativar())) {
            empresa.getModulosAtivos().add(dto.modulo());
        } else {
            empresa.getModulosAtivos().remove(dto.modulo());
        }

        empresaRepository.save(empresa);
        return ResponseEntity.noContent().build();
    }
}