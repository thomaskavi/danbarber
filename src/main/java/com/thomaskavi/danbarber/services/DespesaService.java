package com.thomaskavi.danbarber.services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import com.thomaskavi.danbarber.dtos.DespesaRequestDTO;
import com.thomaskavi.danbarber.dtos.DespesaResponseDTO;
import com.thomaskavi.danbarber.entities.Despesa;
import com.thomaskavi.danbarber.entities.Empresa;
import com.thomaskavi.danbarber.repositories.DespesaRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class DespesaService {

    private final DespesaRepository despesaRepository;
    private final AutenticacaoService autenticacaoService;

    public DespesaResponseDTO registrar(DespesaRequestDTO dto) {
        Empresa empresa = autenticacaoService.obterUsuarioLogado().getEmpresa();

        Despesa despesa = Despesa.builder()
                .descricao(dto.descricao())
                .valor(dto.valor())
                .data(dto.data())
                .categoria(dto.categoria())
                .empresa(empresa)
                .build();

        return toResponseDTO(despesaRepository.save(despesa));
    }

    public List<DespesaResponseDTO> listarPorPeriodo(LocalDate inicio, LocalDate fim) {
        Long empresaId = autenticacaoService.obterEmpresaIdLogado();
        return despesaRepository.findByDataBetweenAndEmpresaId(inicio, fim, empresaId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private DespesaResponseDTO toResponseDTO(Despesa d) {
        return new DespesaResponseDTO(d.getId(), d.getDescricao(), d.getValor(), d.getData(), d.getCategoria());
    }

    public DespesaResponseDTO atualizar(Long id, DespesaRequestDTO dto) {
        Despesa despesa = buscarOuFalhar(id);

        despesa.setDescricao(dto.descricao());
        despesa.setValor(dto.valor());
        despesa.setData(dto.data());
        despesa.setCategoria(dto.categoria());

        return toResponseDTO(despesaRepository.save(despesa));
    }

    public void excluir(Long id) {
        Despesa despesa = buscarOuFalhar(id);
        despesaRepository.delete(despesa);
    }

    private Despesa buscarOuFalhar(Long id) {
        Long empresaId = autenticacaoService.obterEmpresaIdLogado();
        return despesaRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Despesa não encontrada"));
    }
}