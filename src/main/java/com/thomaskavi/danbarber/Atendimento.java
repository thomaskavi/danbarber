package com.thomaskavi.danbarber;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "atendimentos")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Atendimento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "barbeiro_id", nullable = false)
    private Usuario barbeiro;

    // Opcional: nome do cliente, sem precisar de cadastro completo
    private String nomeCliente;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime dataHora = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaPagamento formaPagamento;

    // Snapshot dos serviços feitos + preço no momento (preços podem mudar depois)
    @OneToMany(mappedBy = "atendimento", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<AtendimentoServico> servicos = new ArrayList<>();

    // Calculado a partir da soma dos serviços — guardado para evitar recálculo
    // e para preservar o valor histórico mesmo se preços mudarem depois
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorTotal;

    // Comissão já calculada e "congelada" no momento do atendimento,
    // usando o percentual do barbeiro naquele momento
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valorComissao;

    private String observacao;
}
