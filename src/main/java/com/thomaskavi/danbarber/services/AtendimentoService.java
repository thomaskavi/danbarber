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
import com.thomaskavi.danbarber.enums.Role;
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

        Usuario usuarioLogado = autenticacaoService.obterUsuarioLogado();
        Usuario funcionario = definirFuncionarioDoAtendimento(usuarioLogado, dto.funcionarioId());

        List<Servico> servicos = servicoRepository.findAllById(dto.servicoIds());

        if (servicos.size() != dto.servicoIds().size()) {
            throw new EntityNotFoundException("Um ou mais serviços informados não existem");
        }

        // Garante que todos os serviços informados pertencem à mesma empresa do funcionario
        Long empresaId = usuarioLogado.getEmpresa().getId();
        boolean algumServicoDeOutraEmpresa = servicos.stream()
                .anyMatch(s -> !s.getEmpresa().getId().equals(empresaId));
        if (algumServicoDeOutraEmpresa) {
            throw new EntityNotFoundException("Um ou mais serviços informados não existem");
        }

        Atendimento atendimento = Atendimento.builder()
                .funcionario(funcionario)
                .nomeCliente(dto.nomeCliente())
                .formaPagamento(dto.formaPagamento())
                .observacao(dto.observacao())
                .dataHora(LocalDateTime.now())
                .build();

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

        BigDecimal percentual = funcionario.getPercentualComissao() != null
                ? funcionario.getPercentualComissao()
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
        Long empresaId = autenticacaoService.obterEmpresaIdLogado();
        return atendimentoRepository.findByFuncionario_Empresa_IdAndDataHoraBetween(empresaId, inicio, fim).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<AtendimentoResponseDTO> listarPorFuncionarioEPeriodo(
            Long funcionarioId, LocalDateTime inicio, LocalDateTime fim) {
        Long empresaId = autenticacaoService.obterEmpresaIdLogado();
        return atendimentoRepository
                .findByFuncionarioIdAndFuncionario_Empresa_IdAndDataHoraBetween(funcionarioId, empresaId, inicio, fim)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<AtendimentoResponseDTO> listarMeusAtendimentos(LocalDateTime inicio, LocalDateTime fim) {
        Usuario usuarioLogado = autenticacaoService.obterUsuarioLogado();
        return listarPorFuncionarioEPeriodo(usuarioLogado.getId(), inicio, fim);
    }

    private AtendimentoResponseDTO toResponseDTO(Atendimento a) {
        List<String> nomesServicos = a.getServicos().stream()
                .map(item -> item.getServico().getNome())
                .toList();

        return new AtendimentoResponseDTO(
                a.getId(),
                a.getFuncionario().getNome(),
                a.getNomeCliente(),
                a.getDataHora(),
                a.getFormaPagamento(),
                nomesServicos,
                a.getValorTotal(),
                a.getValorComissao(),
                a.getObservacao()
        );
    }

    private Usuario definirFuncionarioDoAtendimento(Usuario usuarioLogado, Long funcionarioIdInformado) {
        if (usuarioLogado.getRole() == Role.FUNCIONARIO) {
            return usuarioLogado;
        }

        if (funcionarioIdInformado == null) {
            throw new IllegalArgumentException(
                    "Como você está logado como dono, informe o funcionarioId do atendimento");
        }

        Usuario funcionario = usuarioRepository.findById(funcionarioIdInformado)
                .orElseThrow(() -> new EntityNotFoundException("Funcionario não encontrado"));

        // Impede o DONO de lançar atendimento em nome de funcionario de outra empresa
        if (!funcionario.getEmpresa().getId().equals(usuarioLogado.getEmpresa().getId())) {
            throw new EntityNotFoundException("Funcionario não encontrado");
        }

        return funcionario;
    }
}
