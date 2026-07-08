package com.thomaskavi.danbarber.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.thomaskavi.danbarber.entities.Despesa;
import com.thomaskavi.danbarber.repositories.DespesaRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DespesaService {

    private final DespesaRepository despesaRepository;

    public Despesa registrar(DespesaRequestDTO dto) {
        Despesa despesa = Despesa.builder()
                .descricao(dto.descricao())
                .valor(dto.valor())
                .data(dto.data())
                .categoria(dto.categoria())
                .build();

        return despesaRepository.save(despesa);
    }

    public List<Despesa> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return despesaRepository.findByDataBetween(inicio, fim);
    }
}
