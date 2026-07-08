package com.thomaskavi.danbarber.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thomaskavi.danbarber.entities.Servico;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findByAtivoTrue();
}
