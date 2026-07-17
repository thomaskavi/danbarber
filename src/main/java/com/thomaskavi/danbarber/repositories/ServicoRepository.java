package com.thomaskavi.danbarber.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomaskavi.danbarber.entities.Servico;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findByAtivoTrueAndEmpresaId(Long empresaId);
    List<Servico> findByAtivoFalseAndEmpresaId(Long empresaId);
    Optional<Servico> findByIdAndEmpresaId(Long id, Long empresaId);
}
