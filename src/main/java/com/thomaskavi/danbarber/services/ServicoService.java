package com.thomaskavi.danbarber.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.thomaskavi.danbarber.dtos.ServicoRequestDTO;
import com.thomaskavi.danbarber.dtos.ServicoResponseDTO;
import com.thomaskavi.danbarber.entities.Empresa;
import com.thomaskavi.danbarber.entities.Servico;
import com.thomaskavi.danbarber.repositories.ServicoRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ServicoService {

    private final ServicoRepository servicoRepository;
    private final AutenticacaoService autenticacaoService;

    public ServicoResponseDTO registrar(ServicoRequestDTO dto) {
        Empresa empresa = autenticacaoService.obterUsuarioLogado().getEmpresa();

        Servico servico = Servico.builder()
                .nome(dto.nome())
                .preco(dto.preco())
                .empresa(empresa)
                .ativo(true)
                .build();

        return toResponseDTO(servicoRepository.save(servico));
    }

    public List<ServicoResponseDTO> listarAtivos() {
        Long empresaId = autenticacaoService.obterEmpresaIdLogado();
        return servicoRepository.findByAtivoTrueAndEmpresaId(empresaId).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<ServicoResponseDTO> listarInativos() {
        Long empresaId = autenticacaoService.obterEmpresaIdLogado();
        return servicoRepository.findByAtivoFalseAndEmpresaId(empresaId).stream()
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

    public ServicoResponseDTO reativar(Long id) {
        Servico servico = buscarOuFalhar(id);
        servico.setAtivo(true);
        return toResponseDTO(servicoRepository.save(servico));
    }

    // Busca já escopada por empresa — isso é o que impede um DONO
    // de editar/desativar o serviço de outra empresa só adivinhando o ID
    private Servico buscarOuFalhar(Long id) {
        Long empresaId = autenticacaoService.obterEmpresaIdLogado();
        return servicoRepository.findByIdAndEmpresaId(id, empresaId)
                .orElseThrow(() -> new EntityNotFoundException("Serviço não encontrado: id " + id));
    }

    private ServicoResponseDTO toResponseDTO(Servico s) {
        return new ServicoResponseDTO(s.getId(), s.getNome(), s.getPreco(), s.isAtivo());
    }
}
