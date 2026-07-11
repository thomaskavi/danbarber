package com.thomaskavi.danbarber.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.thomaskavi.danbarber.dtos.DespesaRequestDTO;
import com.thomaskavi.danbarber.dtos.DespesaResponseDTO;
import com.thomaskavi.danbarber.entities.Despesa;
import com.thomaskavi.danbarber.repositories.DespesaRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DespesaService {

    private final DespesaRepository despesaRepository;

    public DespesaResponseDTO registrar(DespesaRequestDTO dto) {
        Despesa despesa = Despesa.builder()
                .descricao(dto.descricao())
                .valor(dto.valor())
                .data(dto.data())
                .categoria(dto.categoria())
                .build();

        return toResponseDTO(despesaRepository.save(despesa));
    }

    public List<DespesaResponseDTO> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        return despesaRepository.findByDataBetween(inicio, fim).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private DespesaResponseDTO toResponseDTO(Despesa d) {
        return new DespesaResponseDTO(d.getId(), d.getDescricao(), d.getValor(), d.getData(), d.getCategoria());
    }

    public DespesaResponseDTO atualizar(Long id, DespesaRequestDTO dto) {

        Despesa despesa = despesaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Despesa não encontrada"));

        despesa.setDescricao(dto.descricao());
        despesa.setValor(dto.valor());
        despesa.setData(dto.data());
        despesa.setCategoria(dto.categoria());

        return toResponseDTO(despesaRepository.save(despesa));
    }

    public void excluir(Long id) {

        if (!despesaRepository.existsById(id)) {
            throw new EntityNotFoundException("Despesa não encontrada");
        }

        despesaRepository.deleteById(id);
    }
}
