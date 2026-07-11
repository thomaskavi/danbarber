package com.thomaskavi.danbarber.repositories;

import com.thomaskavi.danbarber.entities.Servico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServicoRepository extends JpaRepository<Servico, Long> {

    List<Servico> findByAtivoTrue();

    List<Servico> findByAtivoFalse();
}
