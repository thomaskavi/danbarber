package com.thomaskavi.danbarber.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.thomaskavi.danbarber.dtos.ServicoRequestDTO;
import com.thomaskavi.danbarber.dtos.ServicoResponseDTO;
import com.thomaskavi.danbarber.entities.Servico;
import com.thomaskavi.danbarber.repositories.ServicoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;

    public ServicoResponseDTO registrar(ServicoRequestDTO dto) {
        Servico servico = Servico.builder()
                .nome(dto.nome())
                .preco(dto.preco())
                .ativo(true)
                .build();

        return toResponseDTO(servicoRepository.save(servico));
    }

    public List<ServicoResponseDTO> listarAtivos() {
        return servicoRepository.findByAtivoTrue().stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public ServicoResponseDTO atualizar(Long id, ServicoRequestDTO dto) {
        Servico servico = buscarOuFalhar(id);
        servico.setNome(dto.nome());
        servico.setPreco(dto.preco());
        return toResponseDTO(servicoRepository.save(servico));
    }

    public void desativar(Long id) {
        Servico servico = buscarOuFalhar(id);
        servico.setAtivo(false);
        servicoRepository.save(servico);
    }

    private Servico buscarOuFalhar(Long id) {
        return servicoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado: id " + id));
    }

    private ServicoResponseDTO toResponseDTO(Servico s) {
        return new ServicoResponseDTO(s.getId(), s.getNome(), s.getPreco(), s.isAtivo());
    }
}
