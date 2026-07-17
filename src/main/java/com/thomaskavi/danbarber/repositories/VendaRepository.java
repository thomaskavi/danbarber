package com.thomaskavi.danbarber.repositories;

import com.thomaskavi.danbarber.entities.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findByVendedor_Empresa_IdAndDataHoraBetween(
            Long empresaId,
            LocalDateTime inicio,
            LocalDateTime fim
    );

    List<Venda> findByVendedorIdAndVendedor_Empresa_IdAndDataHoraBetween(
            Long vendedorId,
            Long empresaId,
            LocalDateTime inicio,
            LocalDateTime fim
    );

    @Query("""
        SELECT COALESCE(SUM(v.valorTotal),0)
        FROM Venda v
        WHERE v.vendedor.empresa.id = :empresaId
          AND v.dataHora BETWEEN :inicio AND :fim
    """)
    BigDecimal somarValorTotalPeriodo(
            Long empresaId,
            LocalDateTime inicio,
            LocalDateTime fim
    );

}