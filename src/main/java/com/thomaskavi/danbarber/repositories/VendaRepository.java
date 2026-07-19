package com.thomaskavi.danbarber.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thomaskavi.danbarber.entities.Venda;

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

    // Espelha o somarComissaoPorFuncionarioEPeriodo do AtendimentoRepository
    @Query("""
        SELECT COALESCE(SUM(v.valorComissao), 0)
        FROM Venda v
        WHERE v.vendedor.id = :vendedorId
          AND v.vendedor.empresa.id = :empresaId
          AND v.dataHora BETWEEN :inicio AND :fim
    """)
    BigDecimal somarComissaoPorVendedorEPeriodo(
            @Param("vendedorId") Long vendedorId,
            @Param("empresaId") Long empresaId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim
    );

}
