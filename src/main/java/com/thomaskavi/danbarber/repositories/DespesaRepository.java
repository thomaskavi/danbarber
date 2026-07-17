package com.thomaskavi.danbarber.repositories;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thomaskavi.danbarber.entities.Despesa;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {

    @Query("""
                    SELECT COALESCE(SUM(d.valor),0)
                    FROM Despesa d
                    WHERE d.empresa.id = :empresaId
                      AND d.data BETWEEN :inicio AND :fim
                    """)
    BigDecimal somarDespesasPorPeriodo(
                    @Param("empresaId") Long empresaId,
                    @Param("inicio") LocalDate inicio,
                    @Param("fim") LocalDate fim);
    List<Despesa> findByDataBetweenAndEmpresaId(LocalDate inicio, LocalDate fim, Long empresaId);

    Optional<Despesa> findByIdAndEmpresaId(Long id, Long empresaId);
}
