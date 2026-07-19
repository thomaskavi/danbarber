package com.thomaskavi.danbarber.services;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.thomaskavi.danbarber.dtos.ItemVendaRequestDTO;
import com.thomaskavi.danbarber.dtos.ItemVendaResponseDTO;
import com.thomaskavi.danbarber.dtos.VendaRequestDTO;
import com.thomaskavi.danbarber.dtos.VendaResponseDTO;
import com.thomaskavi.danbarber.entities.ItemVenda;
import com.thomaskavi.danbarber.entities.Produto;
import com.thomaskavi.danbarber.entities.Usuario;
import com.thomaskavi.danbarber.entities.Venda;
import com.thomaskavi.danbarber.enums.Modulo;
import com.thomaskavi.danbarber.repositories.ProdutoRepository;
import com.thomaskavi.danbarber.repositories.VendaRepository;
import com.thomaskavi.danbarber.security.ModuloGuard;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class VendaService {

    private final VendaRepository vendaRepository;
    private final ProdutoRepository produtoRepository;
    private final AutenticacaoService autenticacaoService;
    private final ModuloGuard moduloGuard;

    @Transactional
    public VendaResponseDTO registrar(VendaRequestDTO dto) {
        moduloGuard.exigirModulo(Modulo.ESTOQUE_VENDAS);

        Usuario vendedor = autenticacaoService.obterUsuarioLogado();
        Long empresaId = vendedor.getEmpresa().getId();

        Venda venda = Venda.builder()
                .vendedor(vendedor)
                .nomeCliente(dto.nomeCliente())
                .formaPagamento(dto.formaPagamento())
                .observacao(dto.observacao())
                .dataHora(LocalDateTime.now(ZoneId.of("America/Sao_Paulo")))
                .build();

        List<ItemVenda> itens = new ArrayList<>();
        BigDecimal valorTotal = BigDecimal.ZERO;

        // Importante: processa item por item, buscando cada produto COM LOCK,
        // e já decrementando o estoque na mesma transação
        for (ItemVendaRequestDTO itemDto : dto.itens()) {
            Produto produto = produtoRepository
                    .buscarComLockPorIdEEmpresa(itemDto.produtoId(), empresaId)
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Produto não encontrado: id " + itemDto.produtoId()));

            if (!produto.isAtivo()) {
                throw new IllegalArgumentException("Produto inativo: " + produto.getNome());
            }

            if (produto.getQuantidadeEstoque() < itemDto.quantidade()) {
                throw new IllegalArgumentException(
                        "Estoque insuficiente para " + produto.getNome() +
                        " (disponível: " + produto.getQuantidadeEstoque() + ")");
            }

            // Baixa do estoque acontece aqui, dentro da mesma transação da venda —
            // se algo falhar depois, o rollback desfaz a baixa também
            produto.setQuantidadeEstoque(produto.getQuantidadeEstoque() - itemDto.quantidade());
            produtoRepository.save(produto);

            BigDecimal subtotal = produto.getPreco().multiply(BigDecimal.valueOf(itemDto.quantidade()));
            valorTotal = valorTotal.add(subtotal);

            ItemVenda item = ItemVenda.builder()
                    .venda(venda)
                    .produto(produto)
                    .quantidade(itemDto.quantidade())
                    .precoUnitarioCobrado(produto.getPreco()) // snapshot do preço no momento da venda
                    .build();

            itens.add(item);
        }

        // Comissão calculada sobre o valor total da venda, com o percentual do vendedor
        // "congelado" no momento — mesmo raciocínio já usado em Atendimento
        BigDecimal percentual = vendedor.getPercentualComissao() != null
                ? vendedor.getPercentualComissao()
                : BigDecimal.ZERO;

        BigDecimal valorComissao = valorTotal
                .multiply(percentual)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        venda.setItens(itens);
        venda.setValorTotal(valorTotal);
        venda.setValorComissao(valorComissao);

        Venda salva = vendaRepository.save(venda);

        return toResponseDTO(salva);
    }

    public List<VendaResponseDTO> listarPorPeriodo(LocalDateTime inicio, LocalDateTime fim) {
        moduloGuard.exigirModulo(Modulo.ESTOQUE_VENDAS);
        Long empresaId = autenticacaoService.obterEmpresaIdLogado();
        return vendaRepository.findByVendedor_Empresa_IdAndDataHoraBetween(empresaId, inicio, fim).stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<VendaResponseDTO> listarMinhasVendas(LocalDateTime inicio, LocalDateTime fim) {
        moduloGuard.exigirModulo(Modulo.ESTOQUE_VENDAS);
        Usuario logado = autenticacaoService.obterUsuarioLogado();
        return vendaRepository
                .findByVendedorIdAndVendedor_Empresa_IdAndDataHoraBetween(
                        logado.getId(), logado.getEmpresa().getId(), inicio, fim)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    private VendaResponseDTO toResponseDTO(Venda v) {
        List<ItemVendaResponseDTO> itensDto = v.getItens().stream()
                .map(i -> new ItemVendaResponseDTO(
                        i.getProduto().getNome(),
                        i.getQuantidade(),
                        i.getPrecoUnitarioCobrado(),
                        i.getPrecoUnitarioCobrado().multiply(BigDecimal.valueOf(i.getQuantidade()))
                ))
                .toList();

        return new VendaResponseDTO(
                v.getId(),
                v.getVendedor().getNome(),
                v.getNomeCliente(),
                v.getDataHora(),
                v.getFormaPagamento(),
                itensDto,
                v.getValorTotal(),
                v.getValorComissao(),
                v.getObservacao()
        );
    }
}
