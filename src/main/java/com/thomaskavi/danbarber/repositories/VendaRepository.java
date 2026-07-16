package com.thomaskavi.danbarber.repositories;

import com.thomaskavi.danbarber.entities.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findByVendedor_Empresa_IdAndDataHoraBetween(
            Long empresaId, LocalDateTime inicio, LocalDateTime fim);

    List<Venda> findByVendedorIdAndVendedor_Empresa_IdAndDataHoraBetween(
            Long vendedorId, Long empresaId, LocalDateTime inicio, LocalDateTime fim);
}