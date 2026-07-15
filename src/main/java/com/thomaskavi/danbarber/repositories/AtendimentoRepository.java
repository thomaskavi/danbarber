package com.thomaskavi.danbarber.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thomaskavi.danbarber.entities.Atendimento;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

        // Soma direta da comissão de um funcionario no período, sem trazer as entidades
        // inteiras para a memória — útil quando o volume de atendimentos crescer
        @Query("""
                        SELECT a.formaPagamento AS formaPagamento,
                               COALESCE(SUM(a.valorTotal),0) AS total
                        FROM Atendimento a
                        WHERE a.funcionario.empresa.id = :empresaId
                          AND a.dataHora BETWEEN :inicio AND :fim
                        GROUP BY a.formaPagamento
                        """)
        List<TotalPorFormaPagamento> somarTotalPorFormaPagamento(
                        @Param("empresaId") Long empresaId,
                        @Param("inicio") LocalDateTime inicio,
                        @Param("fim") LocalDateTime fim);

        interface TotalPorFormaPagamento {
                com.thomaskavi.danbarber.enums.FormaPagamento getFormaPagamento();

                BigDecimal getTotal();
        }

        @Query("""
                        SELECT COALESCE(SUM(a.valorComissao), 0)
                        FROM Atendimento a
                        WHERE a.funcionario.id = :funcionarioId
                          AND a.funcionario.empresa.id = :empresaId
                          AND a.dataHora BETWEEN :inicio AND :fim
                        """)
        BigDecimal somarComissaoPorFuncionarioEPeriodo(
                        @Param("funcionarioId") Long funcionarioId,
                        @Param("empresaId") Long empresaId,
                        @Param("inicio") LocalDateTime inicio,
                        @Param("fim") LocalDateTime fim);

        List<Atendimento> findByFuncionario_Empresa_IdAndDataHoraBetween(
                        Long empresaId,
                        LocalDateTime inicio,
                        LocalDateTime fim);

        List<Atendimento> findByFuncionarioIdAndFuncionario_Empresa_IdAndDataHoraBetween(
                        Long funcionarioId,
                        Long empresaId,
                        LocalDateTime inicio,
                        LocalDateTime fim);
}
