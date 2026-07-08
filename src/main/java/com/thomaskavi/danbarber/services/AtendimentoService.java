package com.thomaskavi.danbarber.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thomaskavi.danbarber.dtos.AtendimentoRequestDTO;
import com.thomaskavi.danbarber.dtos.AtendimentoResponseDTO;
import com.thomaskavi.danbarber.entities.Atendimento;
import com.thomaskavi.danbarber.entities.AtendimentoServico;
import com.thomaskavi.danbarber.entities.Servico;
import com.thomaskavi.danbarber.entities.Usuario;
import com.thomaskavi.danbarber.repositories.AtendimentoRepository;
import com.thomaskavi.danbarber.repositories.ServicoRepository;
import com.thomaskavi.danbarber.repositories.UsuarioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AtendimentoService {

    private final AtendimentoRepository atendimentoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ServicoRepository servicoRepository;
    private final AutenticacaoService autenticacaoService;

    @SuppressWarnings("null")
@Transactional
    public AtendimentoResponseDTO registrar(AtendimentoRequestDTO dto) {

        Usuario barbeiro = usuarioRepository.findById(dto.barbeiroId())
                .orElseThrow(() -> new EntityNotFoundException("Barbeiro não encontrado"));

        List<Servico> servicos = servicoRepository.findAllById(dto.servicoIds());

        if (servicos.size() != dto.servicoIds().size()) {
            throw new EntityNotFoundException("Um ou mais serviços informados não existem");
        }

        Atendimento atendimento = Atendimento.builder()
                .barbeiro(barbeiro)
                .nomeCliente(dto.nomeCliente())
                .formaPagamento(dto.formaPagamento())
                .observacao(dto.observacao())
                .dataHora(LocalDateTime.now())
                .build();

        // Snapshot: guarda o preço de cada serviço no momento do atendimento
        List<AtendimentoServico> itens = servicos.stream()
                .map(servico -> AtendimentoServico.builder()
                        .atendimento(atendimento)
                        .servico(servico)
                        .precoCobrado(servico.getPreco())
                        .build())
                .toList();

        atendimento.setServicos(itens);

        BigDecimal valorTotal = itens.stream()
                .map(AtendimentoServico::getPrecoCobrado)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal percentual = barbeiro.getPercentualComissao() != null
                ? barbeiro.getPercentualComissao()
                : BigDecimal.ZERO;

        BigDecimal valorComissao = valorTotal
                .multiply(percentual)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        atendimento.setValorTotal(valorTotal);
        atendimento.setValorComissao(valorComissao);

        Atendimento salvo = atendimentoRepository.save(atendimento);

        return toResponseDTO(salvo);
    }

    public List<AtendimentoResponseDTO> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        return atendimentoRepository.findByDataHoraBetween(inicio, fim).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<AtendimentoResponseDTO> listarPorBarbeiroEPeriodo(
            Long barbeiroId, LocalDateTime inicio, LocalDateTime fim) {
        return atendimentoRepository.findByBarbeiroIdAndDataHoraBetween(barbeiroId, inicio, fim).stream()
                .map(this::toResponseDTO)
                .toList();
    }

      // Conveniência: o barbeiro logado consulta os próprios atendimentos, sem precisar saber o próprio ID
    public List<AtendimentoResponseDTO> listarMeusAtendimentos(LocalDateTime inicio, LocalDateTime fim) {
        Usuario usuarioLogado = autenticacaoService.obterUsuarioLogado();
        return listarPorBarbeiroEPeriodo(usuarioLogado.getId(), inicio, fim);
    }

    private AtendimentoResponseDTO toResponseDTO(Atendimento a) {
        List<String> nomesServicos = a.getServicos().stream()
                .map(item -> item.getServico().getNome())
                .toList();

        return new AtendimentoResponseDTO(
                a.getId(),
                a.getBarbeiro().getNome(),
                a.getNomeCliente(),
                a.getDataHora(),
                a.getFormaPagamento(),
                nomesServicos,
                a.getValorTotal(),
                a.getValorComissao(),
                a.getObservacao()
        );
    }
}
