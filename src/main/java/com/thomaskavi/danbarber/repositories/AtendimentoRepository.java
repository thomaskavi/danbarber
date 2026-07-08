package com.thomaskavi.danbarber.repositories;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thomaskavi.danbarber.entities.Atendimento;

public interface AtendimentoRepository extends JpaRepository<Atendimento, Long> {

    // Todos os atendimentos de um período (ex: fechamento do mês)
    List<Atendimento> findByDataHoraBetween(LocalDateTime inicio, LocalDateTime fim);

    // Atendimentos de um barbeiro específico em um período
    // (ex: conferir a comissão individual dele)
    List<Atendimento> findByBarbeiroIdAndDataHoraBetween(
            Long barbeiroId, LocalDateTime inicio, LocalDateTime fim);

    // Soma direta da comissão de um barbeiro no período, sem trazer as entidades
    // inteiras para a memória — útil quando o volume de atendimentos crescer
    @Query("""
            SELECT COALESCE(SUM(a.valorComissao), 0)
            FROM Atendimento a
            WHERE a.barbeiro.id = :barbeiroId
            AND a.dataHora BETWEEN :inicio AND :fim
            """)
    BigDecimal somarComissaoPorBarbeiroEPeriodo(
            @Param("barbeiroId") Long barbeiroId,
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    // Soma do faturamento total por forma de pagamento no período
    // (ex: "quanto entrou de Pix esse mês", útil pro fechamento de caixa)
    @Query("""
            SELECT a.formaPagamento AS formaPagamento, COALESCE(SUM(a.valorTotal), 0) AS total
            FROM Atendimento a
            WHERE a.dataHora BETWEEN :inicio AND :fim
            GROUP BY a.formaPagamento
            """)
    List<TotalPorFormaPagamento> somarTotalPorFormaPagamento(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    interface TotalPorFormaPagamento {
        com.thomaskavi.danbarber.enums.FormaPagamento getFormaPagamento();
        BigDecimal getTotal();
    }
}
