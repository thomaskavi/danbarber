package com.thomaskavi.danbarber.entities;

import com.thomaskavi.danbarber.enums.Modulo;
import com.thomaskavi.danbarber.enums.Ramo;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "empresas")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    // Metadado informativo — não trava mais funcionalidade sozinho
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Ramo ramo;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "empresa_modulos", joinColumns = @JoinColumn(name = "empresa_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "modulo")
    @Builder.Default
    private Set<Modulo> modulosAtivos = new HashSet<>();

    @Column(nullable = false)
    @Builder.Default
    private boolean ativa = true;
}