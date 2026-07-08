package com.thomaskavi.danbarber.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thomaskavi.danbarber.entities.Despesa;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {

    List<Despesa> findByDataBetween(LocalDate inicio, LocalDate fim);

    @Query("""
            SELECT COALESCE(SUM(d.valor), 0)
            FROM Despesa d
            WHERE d.data BETWEEN :inicio AND :fim
            """)
    BigDecimal somarDespesasPorPeriodo(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);
}
